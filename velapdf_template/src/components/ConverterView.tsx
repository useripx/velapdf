import React, { useState, useRef } from 'react';
import { AppView, SelectedImage, HistoryItem } from '../types';
import { 
  ArrowLeft, 
  History, 
  UploadCloud, 
  Image as ImageIcon,
  RotateCw, 
  Trash2,
  FileDown,
  Sparkles,
  Plus,
  Compass
} from 'lucide-react';
import { jsPDF } from 'jspdf';

interface ConverterViewProps {
  onNavigate: (view: AppView) => void;
  onConversionSuccess: (item: HistoryItem) => void;
  pageSize: string; // 'A4' | 'LETTER' | 'AUTO'
}

export default function ConverterView({ onNavigate, onConversionSuccess, pageSize: defaultPageSize }: ConverterViewProps) {
  const [selectedImages, setSelectedImages] = useState<SelectedImage[]>([]);
  const [isDragging, setIsDragging] = useState(false);
  const [isConverting, setIsConverting] = useState(false);
  const [progress, setProgress] = useState(0);
  const [pageSize, setPageSize] = useState<string>(defaultPageSize || 'AUTO');
  const [orientation, setOrientation] = useState<'portrait' | 'landscape' | 'auto'>('auto');
  
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Handle files selection
  const handleFiles = (files: FileList | null) => {
    if (!files) return;
    const itemsArray: SelectedImage[] = [];

    Array.from(files).forEach((file) => {
      if (file.type.startsWith('image/')) {
        const previewUrl = URL.createObjectURL(file);
        itemsArray.push({
          id: `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
          file,
          previewUrl,
          rotation: 0
        });
      }
    });

    setSelectedImages((prev) => [...prev, ...itemsArray]);
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    handleFiles(e.dataTransfer.files);
  };

  // Remove individual file
  const removeImage = (id: string, url: string) => {
    setSelectedImages((prev) => prev.filter((img) => img.id !== id));
    URL.revokeObjectURL(url);
  };

  // Clear all selections
  const clearAllSelected = () => {
    selectedImages.forEach((img) => URL.revokeObjectURL(img.previewUrl));
    setSelectedImages([]);
  };

  // Rotate individual image
  const rotateImage = (id: string) => {
    setSelectedImages((prev) =>
      prev.map((img) => {
        if (img.id === id) {
          return { ...img, rotation: (img.rotation + 90) % 360 };
        }
        return img;
      })
    );
  };

  // Move image order up
  const moveUp = (index: number) => {
    if (index === 0) return;
    setSelectedImages((prev) => {
      const copy = [...prev];
      const temp = copy[index];
      copy[index] = copy[index - 1];
      copy[index - 1] = temp;
      return copy;
    });
  };

  // Move image order down
  const moveDown = (index: number) => {
    setSelectedImages((prev) => {
      if (index === prev.length - 1) return prev;
      const copy = [...prev];
      const temp = copy[index];
      copy[index] = copy[index + 1];
      copy[index + 1] = temp;
      return copy;
    });
  };

  // Actual PDF generation logic via jsPDF
  const generatePDF = async () => {
    if (selectedImages.length === 0) return;

    setIsConverting(true);
    setProgress(0);

    // Dynamic animation sequence for conversion transparency loader
    const duration = 1800; // 1.8s
    const steps = 30;
    const interval = duration / steps;
    let currentStep = 0;

    const timer = setInterval(() => {
      currentStep++;
      const percent = Math.min(Math.round((currentStep / steps) * 98), 100);
      setProgress(percent);
    }, interval);

    try {
      // Setup master jsPDF document instance
      const doc = new jsPDF({
        orientation: orientation === 'auto' ? 'portrait' : orientation,
        unit: 'px',
        format: pageSize === 'AUTO' ? 'a4' : pageSize.toLowerCase(),
        compress: true
      });

      // Helper to load image dimensions and get base64
      const processImage = (item: SelectedImage): Promise<{ base64: string, w: number, h: number }> => {
        return new Promise((resolve, reject) => {
          const img = new Image();
          img.src = item.previewUrl;
          img.onload = () => {
            // Create target Canvas for rotation & scaling
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');
            if (!ctx) {
              reject(new Error('Canvas context failed'));
              return;
            }

            // Adjust size based on rotation
            const rotateAngle = (item.rotation * Math.PI) / 180;
            if (item.rotation % 180 !== 0) {
              canvas.width = img.height;
              canvas.height = img.width;
            } else {
              canvas.width = img.width;
              canvas.height = img.height;
            }

            // Draw rotated
            ctx.translate(canvas.width / 2, canvas.height / 2);
            ctx.rotate(rotateAngle);
            ctx.drawImage(img, -img.width / 2, -img.height / 2);

            // Export to JPEG base64 string
            const dataUrl = canvas.toDataURL('image/jpeg', 0.85);
            resolve({
              base64: dataUrl,
              w: canvas.width,
              h: canvas.height
            });
          };
          img.onerror = () => reject(new Error('Image load error'));
        });
      };

      // Process all images to get valid dimensional metrics
      const processedImages = await Promise.all(selectedImages.map((img) => processImage(img)));

      processedImages.forEach((imgData, i) => {
        const isFirstPage = i === 0;
        
        let pWidth = imgData.w;
        let pHeight = imgData.h;
        let pdfPageFormat: any = 'a4';

        // Override settings if not in Auto-Size mode
        if (pageSize !== 'AUTO') {
          // A4 dimensions at 72 points/inch are roughly 595 x 842 points.
          // In px with jsPDF default, we adjust to standard dimension limits.
          pdfPageFormat = pageSize.toLowerCase();
          const standardW = pageSize === 'A4' ? 595 : 612;
          const standardH = pageSize === 'A4' ? 842 : 792;
          
          let targetOrientation = orientation;
          if (targetOrientation === 'auto') {
            targetOrientation = imgData.w > imgData.h ? 'landscape' : 'portrait';
          }

          const pageW = targetOrientation === 'landscape' ? Math.max(standardW, standardH) : Math.min(standardW, standardH);
          const pageH = targetOrientation === 'landscape' ? Math.min(standardW, standardH) : Math.max(standardW, standardH);

          if (!isFirstPage) {
            doc.addPage(pdfPageFormat, targetOrientation);
          } else {
            // Apply first page custom configuration
            doc.setPage(1);
          }

          // Calculate aspect ratio fit of image in custom A4/Letter size
          const scale = Math.min((pageW - 20) / imgData.w, (pageH - 20) / imgData.h);
          const drawW = imgData.w * scale;
          const drawH = imgData.h * scale;
          const xOffset = (pageW - drawW) / 2;
          const yOffset = (pageH - drawH) / 2;

          doc.addImage(imgData.base64, 'JPEG', xOffset, yOffset, drawW, drawH, undefined, 'FAST');
        } else {
          // AUTO PAGE SIZE - Page adjusts exactly to match image size, preserving 100% detail!
          const targetOrientation = pWidth > pHeight ? 'landscape' : 'portrait';
          
          if (!isFirstPage) {
            doc.addPage([pWidth, pHeight], targetOrientation);
          } else {
            // Set first page dimensions
            doc.setPage(1);
            // jsPDF constructor overrides can be bypassed or custom set
            // jsPDF has an internal size manager. We adjust size dynamically:
            // @ts-ignore
            doc.internal.pageSize.width = pWidth;
            // @ts-ignore
            doc.internal.pageSize.height = pHeight;
          }

          doc.addImage(imgData.base64, 'JPEG', 0, 0, pWidth, pHeight, undefined, 'FAST');
        }
      });

      // Get generated base64 data to cache & enable success download
      const pdfBase64 = doc.output('datauristring');
      clearInterval(timer);
      setProgress(100);

      // Extract details for the history list
      const timestamp = Date.now();
      const firstImgName = selectedImages[0].file.name.replace(/\.[^/.]+$/, "");
      const finalFilename = `${firstImgName}_Combined_${selectedImages.length}.pdf`;
      const dateLocalStr = new Date().toLocaleString('id-ID', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });

      // Approx accurate PDF file size
      const bytesLength = pdfBase64.split(',')[1].length * 0.75;
      const formattedSize = bytesLength > 1024 * 1024 
        ? `${(bytesLength / (1024 * 1024)).toFixed(1)} MB` 
        : `${(bytesLength / 1024).toFixed(0)} KB`;

      const generatedHistoryItem: HistoryItem = {
        id: `pdf-${timestamp}`,
        filename: finalFilename,
        fileSize: formattedSize,
        pagesCount: selectedImages.length,
        dateStr: dateLocalStr,
        timestamp,
        pdfDataUrl: pdfBase64,
        thumbnailDataUrl: selectedImages[0].previewUrl // Pass the reference
      };

      // Add small timeout for high fidelity feel
      setTimeout(() => {
        setIsConverting(false);
        onConversionSuccess(generatedHistoryItem);
      }, 300);

    } catch (err) {
      console.error(err);
      alert('Gagal mengonversi file. Silakan periksa kembali format gambar.');
      setIsConverting(false);
      clearInterval(timer);
    }
  };

  return (
    <div className="bg-[#f7f9fb] text-slate-800 min-h-screen flex flex-col font-sans select-none pb-24 md:pb-0">
      
      {/* Loading Overlay */}
      {isConverting && (
        <div className="fixed inset-0 bg-slate-900/80 backdrop-blur-md z-[110] flex flex-col items-center justify-center p-6 text-center shadow-xl animate-fade-in">
          <div className="relative w-24 h-24 mb-6">
            <svg className="animate-spin w-full h-full text-blue-500" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-20" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
              <path className="opacity-100" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4m2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
            </svg>
            <div className="absolute inset-0 flex items-center justify-center font-bold text-sm tracking-widest font-mono text-blue-500">
              {progress}%
            </div>
          </div>
          <h3 className="text-xl font-bold text-white tracking-tight mb-2">Mengonversi Dokumen...</h3>
          <p className="text-slate-400 text-sm max-w-xs leading-relaxed">
            Gambar sedang diproses secara lokal di browser Anda. Harap tunggu beberapa saat...
          </p>

          <div className="w-full max-w-sm mt-8 bg-slate-800 rounded-full h-1.5 overflow-hidden">
            <div className="h-full bg-blue-500 transition-all duration-300" style={{ width: `${progress}%` }} />
          </div>
        </div>
      )}

      {/* Top App Bar */}
      <header className="bg-white border-b border-slate-200 docked flex justify-between items-center w-full px-6 md:px-16 h-16 sticky top-0 z-50">
        <div className="flex items-center gap-4">
          <button 
            id="converter-back-btn"
            onClick={() => { clearAllSelected(); onNavigate('dashboard'); }}
            className="hover:bg-slate-100 transition-colors p-2 rounded-full cursor-pointer active:scale-95 duration-200"
          >
            <ArrowLeft className="w-5 h-5 text-slate-800" />
          </button>
          <span className="text-lg font-bold text-slate-900 tracking-tight font-sans">
            VelaPDF
          </span>
        </div>

        <button 
          onClick={() => { clearAllSelected(); onNavigate('history'); }}
          className="hover:bg-slate-100 transition-colors p-2 rounded-full cursor-pointer active:scale-95 duration-200"
        >
          <History className="w-5 h-5 text-slate-800" />
        </button>
      </header>

      {/* Main Content Area */}
      <main className="w-full max-w-7xl mx-auto px-6 md:px-16 py-8 flex-grow flex flex-col gap-8">
        
        {/* Title area */}
        <div className="flex flex-col gap-2">
          <h2 className="text-3xl font-extrabold text-slate-900 tracking-tight">
            Image to PDF
          </h2>
          <p className="text-sm md:text-base text-slate-500 max-w-xl">
            Convert your photos, screenshots, and scans into professional PDF documents in seconds.
          </p>
        </div>

        {/* Configurations Toolbar drawer */}
        {selectedImages.length > 0 && (
          <div className="bg-white border border-slate-200 p-4 rounded-2xl flex flex-wrap items-center justify-between gap-4 shadow-sm">
            <div className="flex items-center gap-3">
              <span className="text-xs font-bold text-slate-400 font-mono tracking-wider uppercase">Konfigurasi Halaman:</span>
            </div>
            
            <div className="flex flex-wrap items-center gap-4 text-xs">
              <div className="flex items-center gap-2">
                <span className="font-semibold text-slate-500">Ukuran:</span>
                <select 
                  value={pageSize}
                  onChange={(e) => setPageSize(e.target.value)}
                  className="bg-slate-100 font-medium text-slate-800 border-none outline-none rounded-lg px-3 py-1.5 focus:ring-1 focus:ring-blue-500 cursor-pointer"
                >
                  <option value="AUTO">Auto-fit (Detail Utuh)</option>
                  <option value="A4">A4 (Standard)</option>
                  <option value="LETTER">Letter (Kertas Surat)</option>
                </select>
              </div>

              <div className="flex items-center gap-2">
                <span className="font-semibold text-slate-500">Orientasi:</span>
                <select 
                  value={orientation}
                  onChange={(e: any) => setOrientation(e.target.value)}
                  className="bg-slate-100 font-medium text-slate-800 border-none outline-none rounded-lg px-3 py-1.5 focus:ring-1 focus:ring-blue-500 cursor-pointer"
                  disabled={pageSize === 'AUTO'}
                >
                  <option value="auto">Auto Orientasi</option>
                  <option value="portrait">Tegak (Portrait)</option>
                  <option value="landscape">Mendatar (Landscape)</option>
                </select>
              </div>
            </div>
          </div>
        )}

        {/* Upload Selection / Drop zone */}
        {selectedImages.length === 0 ? (
          <label 
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
            className={`relative group cursor-pointer block border-2 border-dashed rounded-3xl p-8 bg-white transition-all duration-300 max-w-4xl mx-auto w-full aspect-[4/3] md:aspect-[21/9] ${
              isDragging ? 'border-blue-500 bg-blue-50/20 scale-102 shadow-md' : 'border-slate-300 hover:border-blue-400 hover:shadow-md'
            }`}
            htmlFor="image-upload"
          >
            <input 
              accept="image/*" 
              className="hidden" 
              id="image-upload" 
              multiple 
              onChange={(e) => handleFiles(e.target.files)} 
              type="file" 
              ref={fileInputRef}
            />
            <div className="w-full h-full flex flex-col items-center justify-center gap-4 text-center">
              <div className="w-16 h-16 rounded-full bg-blue-50 flex items-center justify-center text-blue-600 transition-transform duration-300 group-hover:scale-110 shadow-sm">
                <UploadCloud className="w-8 h-8" />
              </div>
              <div>
                <p className="text-xl font-bold text-slate-900 font-sans">Upload Images</p>
                <p className="text-slate-400 text-xs mt-1 max-w-md mx-auto leading-relaxed">
                  Pilih atau seret gambar, screenshot, atau foto pindaian dari perangkat Anda (Mendukung JPG, PNG, HEIC).
                </p>
              </div>
            </div>
          </label>
        ) : (
          /* Multi-image preview canvas view */
          <div className="flex flex-col gap-4 animate-[fadeIn_0.4s_ease-out]">
            <div className="flex items-center justify-between border-b border-slate-200 pb-2">
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold text-slate-400 font-mono uppercase tracking-wider">Gambar Terpilih ({selectedImages.length})</span>
                <button 
                  onClick={() => fileInputRef.current?.click()}
                  className="bg-blue-50 text-blue-600 hover:bg-blue-100 text-xs font-bold px-3 py-1 rounded-lg flex items-center gap-1 cursor-pointer transition-colors active:scale-95"
                >
                  <Plus className="w-3.5 h-3.5" />
                  Tambah
                </button>
              </div>
              <button 
                onClick={clearAllSelected}
                className="text-red-500 hover:text-red-600 text-xs font-semibold cursor-pointer active:scale-95"
              >
                Hapus Semua ({selectedImages.length})
              </button>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
              {selectedImages.map((image, index) => (
                <div 
                  key={image.id}
                  className="bg-white border border-slate-200/80 rounded-2xl overflow-hidden shadow-sm flex flex-col justify-between group relative hover:shadow-md hover:border-slate-300 transition-all"
                >
                  {/* Position number indicator */}
                  <span className="absolute top-2 left-2 z-10 bg-slate-900/60 text-white font-mono font-bold text-[10px] w-5 h-5 rounded-full flex items-center justify-center">
                    {index + 1}
                  </span>

                  {/* Rotate and Trash Controls */}
                  <div className="absolute top-2 right-2 z-10 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                    <button 
                      onClick={() => rotateImage(image.id)}
                      className="p-1.5 bg-white/90 backdrop-blur-sm shadow hover:bg-white text-slate-700 rounded-lg cursor-pointer max-xs:p-1"
                      title="Rotate 90°"
                    >
                      <RotateCw className="w-3.5 h-3.5" />
                    </button>
                    <button 
                      onClick={() => removeImage(image.id, image.previewUrl)}
                      className="p-1.5 bg-red-50/90 backdrop-blur-sm shadow hover:bg-red-500 hover:text-white text-red-500 rounded-lg cursor-pointer max-xs:p-1"
                      title="Hapus"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>

                  {/* Thumbnail Preview rendering the rotation state */}
                  <div className="aspect-[4/5] bg-slate-900 flex items-center justify-center overflow-hidden border-b border-slate-100 select-none">
                    <img 
                      alt={`Preview ${index + 1}`} 
                      className="max-h-full max-w-full object-contain transition-transform duration-300" 
                      src={image.previewUrl}
                      style={{ transform: `rotate(${image.rotation}deg)` }}
                    />
                  </div>

                  {/* Metadata and order controller bottom container */}
                  <div className="p-2 py-2.5 flex flex-col gap-1.5 bg-slate-50/50">
                    <p className="text-slate-800 text-[11px] font-bold truncate max-w-full" title={image.file.name}>
                      {image.file.name}
                    </p>
                    <div className="flex items-center justify-between text-[10px] text-slate-400 font-mono">
                      <span>{(image.file.size / 1024).toFixed(0)} KB</span>
                      <div className="flex gap-1.5">
                        <button 
                          onClick={() => moveUp(index)}
                          className="hover:text-blue-500 cursor-pointer disabled:text-slate-200 disabled:cursor-not-allowed"
                          disabled={index === 0}
                        >
                          Latar Belakang
                        </button>
                        <button 
                          onClick={() => moveDown(index)}
                          className="hover:text-blue-500 cursor-pointer disabled:text-slate-200 disabled:cursor-not-allowed"
                          disabled={index === selectedImages.length - 1}
                        >
                          Latar Depan
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Image upload hidden fallback input */}
        <input 
          accept="image/*" 
          className="hidden" 
          id="image-upload" 
          multiple 
          onChange={(e) => handleFiles(e.target.files)} 
          type="file" 
          ref={fileInputRef}
        />

        {/* Empty placeholder view block */}
        {selectedImages.length === 0 && (
          <div className="flex flex-col items-center justify-center py-20 bg-slate-100 rounded-3xl border border-slate-200/60 opacity-60 text-center max-w-4xl mx-auto w-full mb-10">
            <ImageIcon className="w-16 h-16 text-slate-400 mb-4 stroke-[1.2]" />
            <p className="text-slate-500 text-sm font-semibold tracking-wide">No image selected for conversion</p>
          </div>
        )}

      </main>

      {/* Sticky Bottom Converter Trigger Bar */}
      <div className="fixed bottom-0 left-0 w-full bg-white/80 backdrop-blur-md border-t border-slate-200 px-6 md:px-16 py-4 z-40 shadow-md">
        <div className="max-w-7xl mx-auto flex flex-col gap-2">
          {selectedImages.length > 0 ? (
            <button 
              id="convert-trigger-btn"
              onClick={generatePDF}
              className="w-full py-4 rounded-xl font-bold bg-blue-600 text-white hover:bg-blue-700 active:scale-[0.98] transition-all duration-300 flex items-center justify-center gap-3 shadow-lg cursor-pointer"
            >
              <FileDown className="w-5 h-5" />
              <span>Convert to PDF</span>
            </button>
          ) : (
            <button 
              disabled
              className="w-full py-4 rounded-xl bg-slate-200 text-slate-400 font-semibold text-sm flex items-center justify-center gap-2 cursor-not-allowed opacity-50"
            >
              <FileDown className="w-5 h-5 text-slate-300" />
              <span>Convert to PDF</span>
            </button>
          )}
        </div>
      </div>

    </div>
  );
}
