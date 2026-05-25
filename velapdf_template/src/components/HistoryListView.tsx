import React from 'react';
import { AppView, HistoryItem } from '../types';
import { 
  ArrowLeft, 
  Trash2, 
  Download, 
  FolderOpen, 
  Trash,
  ChevronRight,
  FileCheck2,
  Settings as SettingsIcon,
  History as HistoryIcon
} from 'lucide-react';

interface HistoryListViewProps {
  history: HistoryItem[];
  onNavigate: (view: AppView) => void;
  onDeleteHistoryItem: (id: string) => void;
  onClearHistory: () => void;
}

export default function HistoryListView({ 
  history, 
  onNavigate, 
  onDeleteHistoryItem, 
  onClearHistory 
}: HistoryListViewProps) {

  const triggerDownload = (item: HistoryItem) => {
    try {
      const link = document.createElement('a');
      link.href = item.pdfDataUrl;
      link.download = item.filename;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (err) {
      console.error(err);
      alert('Gagal mendownload PDF.');
    }
  };

  return (
    <div className="bg-[#f7f9fb] text-slate-800 min-h-screen flex flex-col font-sans select-none pb-24 md:pb-0">
      
      {/* Top App Bar */}
      <header className="bg-white border-b border-slate-200 docked flex justify-between items-center w-full px-6 md:px-16 h-16 sticky top-0 z-50">
        <div className="flex items-center gap-4">
          <button 
            id="history-back-btn"
            onClick={() => onNavigate('dashboard')}
            className="hover:bg-slate-100 transition-colors p-2 rounded-full cursor-pointer active:scale-95 duration-200"
          >
            <ArrowLeft className="w-5 h-5 text-slate-800" />
          </button>
          <span className="text-lg font-bold text-slate-900 tracking-tight font-sans">
            Riwayat Konversi
          </span>
        </div>

        {history.length > 0 && (
          <button 
            onClick={onClearHistory}
            className="text-red-500 hover:text-red-600 text-xs font-semibold flex items-center gap-1 hover:underline cursor-pointer"
          >
            <Trash className="w-4 h-4" />
            Hapus Semua
          </button>
        )}
      </header>

      {/* Main Container */}
      <main className="flex-grow w-full max-w-7xl mx-auto px-6 md:px-16 py-8 flex flex-col gap-6">
        
        <div className="flex flex-col gap-2">
          <h2 className="text-3xl font-extrabold text-slate-900 tracking-tight">
            Berkas PDF Saya
          </h2>
          <p className="text-slate-500 text-sm md:text-base">
            Daftar seluruh berkas PDF yang telah dikonversi secara lokal di perangkat Anda.
          </p>
        </div>

        {/* List of converted files */}
        {history.length === 0 ? (
          <div className="flex flex-col items-center justify-center p-20 bg-white rounded-3xl border border-slate-200/60 shadow-sm text-center max-w-xl mx-auto w-full mt-10">
            <FolderOpen className="w-16 h-16 text-slate-300 mb-4 stroke-[1.2]" />
            <p className="text-slate-600 font-bold mb-1">Riwayat Konversi Kosong</p>
            <p className="text-slate-400 text-xs leading-relaxed max-w-xs mb-6">
              Mulai konversi foto pindaian Anda menjadi berkas PDF berkualitas tinggi hari ini.
            </p>
            <button 
              onClick={() => onNavigate('converter')}
              className="bg-blue-600 hover:bg-blue-700 text-white font-semibold text-xs px-6 py-3 rounded-lg active:scale-95 transition-all text-center cursor-pointer shadow-sm"
            >
              Konversi Sekarang
            </button>
          </div>
        ) : (
          <div className="flex flex-col gap-3 animate-fade-in max-w-4xl w-full mx-auto">
            {history.map((item) => (
              <div 
                key={item.id}
                className="bg-white border border-slate-200/60 p-4 rounded-2xl flex items-center justify-between gap-4 hover:shadow-md transition-shadow group"
              >
                {/* Thumbnail thumbnail preview or icon */}
                <div className="w-16 h-20 bg-slate-900 rounded-xl overflow-hidden shadow-sm flex-shrink-0 flex items-center justify-center border border-slate-100 select-none">
                  {item.thumbnailDataUrl ? (
                    <img 
                      alt="Thumbnail" 
                      className="w-full h-full object-contain" 
                      src={item.thumbnailDataUrl}
                    />
                  ) : (
                    <div className="text-red-500 font-bold text-xs">PDF</div>
                  )}
                </div>

                {/* File Details */}
                <div className="flex-grow text-left overflow-hidden">
                  <h4 className="font-bold text-slate-800 text-sm md:text-base truncate max-w-full" title={item.filename}>
                    {item.filename}
                  </h4>
                  <p className="text-slate-400 text-xs font-semibold font-mono tracking-wide mt-1">
                    {item.fileSize} • {item.pagesCount} Halaman
                  </p>
                  <p className="text-[10px] text-slate-400 font-sans mt-1">
                    Dibuat pada {item.dateStr}
                  </p>
                </div>

                {/* Action buttons (Download & Delete) */}
                <div className="flex items-center gap-1 flex-shrink-0">
                  <button 
                    onClick={() => triggerDownload(item)}
                    className="p-2.5 hover:bg-blue-50 hover:text-blue-600 text-slate-500 rounded-xl cursor-pointer active:scale-90 duration-150 transition-colors"
                    title="Download PDF"
                  >
                    <Download className="w-5 h-5" />
                  </button>
                  <button 
                    onClick={() => onDeleteHistoryItem(item.id)}
                    className="p-2.5 hover:bg-red-50 hover:text-red-500 text-slate-400 rounded-xl cursor-pointer active:scale-90 duration-150 transition-colors"
                    title="Hapus dari Riwayat"
                  >
                    <Trash2 className="w-5 h-5" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

      </main>

      {/* Bottom Sticky Mobile Navigation */}
      <nav className="fixed bottom-0 left-0 w-full z-40 flex justify-around items-center h-16 pb-safe bg-white border-t border-slate-200/80 shadow-lg md:hidden">
        <button 
          onClick={() => onNavigate('dashboard')}
          className="flex flex-col items-center justify-center text-slate-400 hover:text-blue-500 transition-all p-2 cursor-pointer flex-1"
        >
          <FileCheck2 className="w-5 h-5 mb-0.5" />
          <span className="text-[10px] tracking-wide uppercase">Convert</span>
        </button>
        <button 
          onClick={() => onNavigate('history')}
          className="flex flex-col items-center justify-center text-blue-600 font-bold transition-all p-2 cursor-pointer flex-1"
        >
          <HistoryIcon className="w-5 h-5 mb-0.5" />
          <span className="text-[10px] tracking-wide uppercase">Files</span>
        </button>
        <button 
          onClick={() => onNavigate('settings')}
          className="flex flex-col items-center justify-center text-slate-400 hover:text-blue-500 transition-all p-2 cursor-pointer flex-1"
        >
          <SettingsIcon className="w-5 h-5 mb-0.5" />
          <span className="text-[10px] tracking-wide uppercase">Settings</span>
        </button>
      </nav>

    </div>
  );
}
