import React, { useState } from 'react';
import { AppView, HistoryItem } from '../types';
import { 
  CheckCircle,
  FileCheck2,
  Download,
  Share2,
  Check,
  ChevronRight,
  ArrowRight
} from 'lucide-react';

interface SuccessViewProps {
  item: HistoryItem | null;
  onNavigate: (view: AppView) => void;
}

export default function SuccessView({ item, onNavigate }: SuccessViewProps) {
  const [copiedLink, setCopiedLink] = useState(false);

  if (!item) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-[#f7f9fb] p-6 text-center">
        <p className="text-slate-500 font-medium">Data konversi tidak ditemukan.</p>
        <button 
          onClick={() => onNavigate('dashboard')}
          className="mt-4 px-4 py-2 bg-blue-600 text-white font-bold rounded-lg"
        >
          Kembali ke Dashboard
        </button>
      </div>
    );
  }

  // Trigger actual download of base64 compiled PDF in browser
  const handleDownload = () => {
    try {
      const link = document.createElement('a');
      link.href = item.pdfDataUrl;
      link.download = item.filename;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (err) {
      console.error('Trigger download failed', err);
      alert('Gagal mendownload PDF. Silakan coba kembali.');
    }
  };

  // Simulate or trigger browser Web Share
  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: item.filename,
          text: 'VelaPDF - Dokumen hasil konversi Anda telah siap digunakan.',
          url: window.location.href
        });
      } catch (err) {
        console.log('User cancelled or Web Share failed', err);
      }
    } else {
      // Fallback: Copy generic temporary link & notify
      navigator.clipboard.writeText(`${window.location.origin}/share/${item.id}`);
      setCopiedLink(true);
      setTimeout(() => setCopiedLink(false), 2500);
    }
  };

  return (
    <div className="min-h-screen bg-slate-900/10 backdrop-blur-md flex items-center justify-center p-4 md:p-6 animate-fade-in select-none">
      
      {/* Absolute floating background card mimicking native modular wrapper */}
      <div className="bg-white w-full max-w-[480px] rounded-3xl shadow-xl border border-slate-200/80 p-6 md:p-8 flex flex-col items-center relative overflow-hidden animate-[slideUp_0.4s_ease-out]">
        
        {/* Decorative corner background aura and pulses */}
        <div className="absolute top-0 right-0 w-32 h-32 bg-blue-500/5 rounded-full blur-2xl" />
        <div className="absolute bottom-0 left-0 w-32 h-32 bg-emerald-500/5 rounded-full blur-2xl" />

        {/* Pulsating Native Success Checkmark Mark */}
        <div className="relative w-20 h-20 bg-emerald-50 text-emerald-500 rounded-full flex items-center justify-center mb-6 ring-8 ring-emerald-500/5 animate-[scalePulse_2s_infinite_ease-in-out]">
          <CheckCircle className="w-10 h-10 stroke-[2.2]" />
        </div>

        {/* Header Text */}
        <h2 className="text-2xl md:text-3xl font-extrabold text-slate-900 text-center tracking-tight mb-2">
          Conversion Successful
        </h2>
        <p className="text-slate-500 text-center text-sm max-w-[325px] leading-relaxed mb-6">
          Your document is parsed, compiled, and ready for professional use.
        </p>

        {/* Visual Summary Card representation */}
        <div className="w-full bg-slate-50 border border-slate-200/60 rounded-2xl p-4 flex items-center gap-4 mb-6 hover:shadow-xs transition-shadow">
          <div className="w-12 h-12 bg-white rounded-xl flex items-center justify-center border border-slate-200 text-red-500 shadow-sm flex-shrink-0">
            <svg viewBox="0 0 24 24" width="24" height="24" className="stroke-current fill-current">
              <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-9.5 8.5H8V13h1.5c.83 0 1.5-.67 1.5-1.5s-.67-1.5-1.5-1.5zm5 2H13V9h1.5c.83 0 1.5.67 1.5 1.5v1c0 .83-.67 1.5-1.5 1.5zm-5-3.5H8v1.5h1.5c.28 0 .5-.22.5-.5V10c0-.28-.22-.5-.5-.5z" />
            </svg>
          </div>
          <div className="text-left overflow-hidden flex-grow">
            <p className="font-bold text-slate-800 text-sm truncate max-w-full" title={item.filename}>
              {item.filename}
            </p>
            <p className="text-slate-400 text-xs font-semibold font-mono tracking-wide mt-0.5">
              {item.fileSize} • {item.pagesCount} Halaman
            </p>
          </div>
        </div>

        {/* Copy Notification Toast Panel */}
        {copiedLink && (
          <div className="w-full p-3 bg-emerald-50 border border-emerald-200 rounded-xl flex items-center gap-2 mb-4 text-emerald-800 text-xs font-semibold animate-fade-in">
            <Check className="w-4 h-4 text-emerald-600 stroke-[2.5]" />
            <span>Tautan salinan clipboard dibuat!</span>
          </div>
        )}

        {/* Dynamic Action Buttons Stack */}
        <div className="w-full flex flex-col gap-3">
          
          {/* Primary: Save to Local Device */}
          <button 
            id="download-save-btn"
            onClick={handleDownload}
            className="w-full bg-[#0058be] text-white py-3.5 rounded-xl font-bold hover:bg-[#004395] active:scale-[0.98] transition-all flex items-center justify-center gap-2 shadow-md cursor-pointer"
          >
            <Download className="w-5 h-5 fill-current" />
            <span>Save to Device</span>
          </button>

          {/* Secondary: Share compiled base64 package link */}
          <button 
            id="share-file-btn"
            onClick={handleShare}
            className="w-full border border-blue-500/30 bg-white hover:bg-blue-50/10 text-blue-600 py-3.5 rounded-xl font-bold active:scale-[0.98] transition-all flex items-center justify-center gap-2 cursor-pointer"
          >
            <Share2 className="w-5 h-5" />
            <span>Share PDF</span>
          </button>

        </div>

        {/* Nav dismissal trigger */}
        <button 
          id="success-dismiss-btn"
          onClick={() => onNavigate('dashboard')}
          className="mt-6 text-sm font-semibold text-slate-400 hover:text-slate-700 hover:underline transition-colors cursor-pointer"
        >
          Done
        </button>

      </div>

    </div>
  );
}
