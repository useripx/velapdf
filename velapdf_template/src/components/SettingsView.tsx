import React, { useState } from 'react';
import { AppView, HistoryItem } from '../types';
import { 
  ArrowLeft, 
  Trash2, 
  Settings, 
  Check, 
  Sliders, 
  Layers, 
  ShieldCheck, 
  HardDrive,
  FileCheck2,
  History as HistoryIcon,
  RotateCcw
} from 'lucide-react';

interface SettingsViewProps {
  onNavigate: (view: AppView) => void;
  pageSize: string;
  onPageSizeChange: (size: string) => void;
  historyCount: number;
  onWipeAllHistory: () => void;
}

export default function SettingsView({ 
  onNavigate, 
  pageSize, 
  onPageSizeChange, 
  historyCount, 
  onWipeAllHistory 
}: SettingsViewProps) {
  const [saveStatus, setSaveStatus] = useState(false);

  const handlePageSizeChange = (val: string) => {
    onPageSizeChange(val);
    setSaveStatus(true);
    setTimeout(() => setSaveStatus(false), 2000);
  };

  const handleWipe = () => {
    const check = confirm('Apakah Anda yakin ingin menghapus seluruh riwayat berkas PDF lokal Anda? File yang disimpan dalam Riwayat akan dihapus permanen.');
    if (check) {
      onWipeAllHistory();
      alert('Seluruh berkas lokal berhasil dihapus.');
    }
  };

  return (
    <div className="bg-[#f7f9fb] text-slate-800 min-h-screen flex flex-col font-sans select-none pb-24 md:pb-0">
      
      {/* Top App Bar */}
      <header className="bg-white border-b border-slate-200 docked flex justify-between items-center w-full px-6 md:px-16 h-16 sticky top-0 z-50">
        <div className="flex items-center gap-4">
          <button 
            id="settings-back-btn"
            onClick={() => onNavigate('dashboard')}
            className="hover:bg-slate-100 transition-colors p-2 rounded-full cursor-pointer active:scale-95 duration-200"
          >
            <ArrowLeft className="w-5 h-5 text-slate-800" />
          </button>
          <span className="text-lg font-bold text-slate-900 tracking-tight font-sans">
            Pengaturan
          </span>
        </div>

        <Settings className="w-5 h-5 text-slate-400" />
      </header>

      {/* Main Container */}
      <main className="flex-grow w-full max-w-7xl mx-auto px-6 md:px-16 py-8 flex flex-col gap-6 max-w-2xl">
        
        <div className="flex flex-col gap-2">
          <h2 className="text-3xl font-extrabold text-slate-900 tracking-tight">
            Preferensi Perangkat
          </h2>
          <p className="text-slate-500 text-sm">
            Atur default konfigurasi rendering gambar dan optimasi memori secara instan.
          </p>
        </div>

        {/* Saved Status Notification */}
        {saveStatus && (
          <div className="p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs font-semibold rounded-xl flex items-center gap-2 animate-fade-in">
            <Check className="w-4 h-4 text-emerald-600 stroke-[2.5]" />
            <span>Format halaman bawaan berhasil diubah!</span>
          </div>
        )}

        {/* Setting Section 1: Base Configurations */}
        <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden divide-y divide-slate-100">
          
          <div className="p-4 flex flex-col gap-2">
            <div className="flex items-center gap-2 text-slate-400">
              <Sliders className="w-4 h-4" />
              <span className="text-xs font-bold font-mono tracking-wide uppercase">Kertas &amp; Dimensi Bawaan</span>
            </div>
            <p className="text-xs text-slate-400 max-w-md">Layout halaman otomatis akan meng-overscale/under-scale sesuai rasio file gambar terpilih.</p>
            <div className="mt-3">
              <select 
                value={pageSize}
                onChange={(e) => handlePageSizeChange(e.target.value)}
                className="bg-slate-100 font-semibold text-slate-800 text-sm border-none outline-none rounded-xl px-4 py-2.5 focus:ring-1 focus:ring-blue-500 cursor-pointer w-full"
              >
                <option value="AUTO">Auto-fit (Pas sesuai ukuran foto)</option>
                <option value="A4">A4 (Standard Indonesia)</option>
                <option value="LETTER">Letter (Kertas Kuarto)</option>
              </select>
            </div>
          </div>

          <div className="p-4 flex flex-col gap-2">
            <div className="flex items-center gap-2 text-slate-400">
              <Layers className="w-4 h-4" />
              <span className="text-xs font-bold font-mono tracking-wide uppercase">Kualitas Gambar</span>
            </div>
            <p className="text-xs text-slate-400 max-w-sm">Resolusi kompresi standard mengamankan detail di 150 DPI untuk menyeimbangkan ukuran byte.</p>
            <div className="mt-2">
              <span className="text-xs font-bold text-emerald-600 bg-emerald-50 px-3 py-1 rounded-full uppercase">Optimal (Default)</span>
            </div>
          </div>

        </div>

        {/* Setting Section 2: Storage Sandbox Details */}
        <div className="bg-white border border-slate-200 rounded-2xl shadow-sm p-4 flex flex-col gap-4">
          
          <div className="flex items-center gap-2 text-slate-400">
            <HardDrive className="w-4 h-4" />
            <span className="text-xs font-bold font-mono tracking-wide uppercase">Penyimpanan Cache</span>
          </div>

          <div className="flex justify-between items-center text-sm border-b border-slate-100 pb-3">
            <div className="text-left">
              <span className="font-semibold text-slate-700 block">Riwayat PDF Terdaftar</span>
              <span className="text-xs text-slate-400">Tersimpan dalam memory localStorage</span>
            </div>
            <span className="font-bold text-slate-800 text-base">{historyCount} Berkas</span>
          </div>

          <div className="flex justify-between items-center pt-1">
            <div className="text-left w-2/3">
              <span className="font-semibold text-slate-700 block">Kosongkan Riwayat Lokal</span>
              <span className="text-xs text-slate-400 block mt-0.5">Wipe data cache untuk membebaskan ruang di browser Anda.</span>
            </div>
            <button 
              id="settings-wipe-btn"
              onClick={handleWipe}
              className="bg-red-50 text-red-600 hover:bg-red-100 font-bold text-xs px-4 py-2.5 rounded-xl cursor-pointer active:scale-95 duration-150 transition-colors"
            >
              Hapus Semua
            </button>
          </div>

        </div>

        {/* Compliance Footer Shield */}
        <div className="bg-slate-900 text-white rounded-2xl p-5 flex items-start gap-4 shadow-md">
          <div className="p-2.5 bg-blue-500/10 rounded-xl text-blue-400 flex-shrink-0">
            <ShieldCheck className="w-6 h-6 stroke-[1.8]" />
          </div>
          <div>
            <h5 className="font-bold text-sm tracking-tight mb-1 text-white">VelaPDF Security Compliance</h5>
            <p className="text-slate-400 text-xs leading-relaxed">
              Seluruh rekayasa berkas PDF, rotasi visual, dan pembacaan metadata foto diselesaikan 100% menggunakan API lokal browser Chrome/Node. Kami memberikan garansi data privasi penuh demi kenyamanan Anda.
            </p>
          </div>
        </div>

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
          className="flex flex-col items-center justify-center text-slate-400 hover:text-blue-500 transition-all p-2 cursor-pointer flex-1"
        >
          <HistoryIcon className="w-5 h-5 mb-0.5" />
          <span className="text-[10px] tracking-wide uppercase">Files</span>
        </button>
        <button 
          onClick={() => onNavigate('settings')}
          className="flex flex-col items-center justify-center text-blue-600 font-bold transition-all p-2 cursor-pointer flex-1"
        >
          <Settings className="w-5 h-5 mb-0.5" />
          <span className="text-[10px] tracking-wide uppercase">Settings</span>
        </button>
      </nav>

    </div>
  );
}
