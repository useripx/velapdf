import React, { useState } from 'react';
import { AppView } from '../types';
import { 
  FileImage, 
  FileText, 
  TableProperties, 
  ArrowRight,
  Shield, 
  History, 
  Menu, 
  Settings as SettingsIcon,
  HelpCircle,
  X,
  FileCheck2
} from 'lucide-react';

interface DashboardViewProps {
  onNavigate: (view: AppView) => void;
}

export default function DashboardView({ onNavigate }: DashboardViewProps) {
  const [showPrivacyModal, setShowPrivacyModal] = useState(false);
  const [showSidebar, setShowSidebar] = useState(false);

  return (
    <div className="bg-[#f7f9fb] text-slate-800 min-h-screen flex flex-col font-sans select-none overflow-x-hidden pb-16 md:pb-0">
      
      {/* Top Header */}
      <header className="bg-white/80 backdrop-blur-md border-b border-slate-200/60 sticky top-0 flex justify-between items-center w-full px-6 md:px-16 h-16 z-50">
        <div className="flex items-center gap-4">
          <button 
            id="menu-trigger-btn"
            onClick={() => setShowSidebar(true)}
            className="hover:bg-slate-100 transition-colors cursor-pointer p-2 rounded-lg active:scale-95 duration-200"
          >
            <Menu className="w-5 h-5 text-slate-800" />
          </button>
          
          <div className="flex items-center gap-2 cursor-pointer" onClick={() => onNavigate('dashboard')}>
            <svg 
              width="32" 
              height="32" 
              viewBox="0 0 512 512" 
              className="drop-shadow-sm"
            >
              <rect x="80" y="40" width="352" height="432" rx="42" fill="#0f172a" />
              <rect x="160" y="160" width="192" height="28" rx="14" fill="#ffffff" />
              <rect x="160" y="240" width="192" height="28" rx="14" fill="#ffffff" />
              <path 
                d="M170 340 L250 420 L400 240" 
                fill="none" 
                stroke="#3B82F6" 
                strokeWidth="48" 
                strokeLinecap="round" 
                strokeLinejoin="round" 
              />
            </svg>
            <h1 className="text-xl font-bold text-slate-900 tracking-tight font-sans">
              VelaPDF
            </h1>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <button 
            id="header-history-btn"
            onClick={() => onNavigate('history')}
            className="hover:bg-slate-100 transition-colors p-2 rounded-full cursor-pointer active:scale-95 duration-200"
            title="Lihat Riwayat"
          >
            <History className="w-5 h-5 text-slate-800" />
          </button>
        </div>
      </header>

      {/* Main Container */}
      <main className="flex-grow w-full max-w-7xl mx-auto px-6 md:px-16 py-8 flex flex-col gap-8 mb-10">
        
        {/* Hero Section */}
        <section className="animate-[fadeIn_0.5s_ease-out]">
          <h2 className="text-4xl md:text-5xl font-extrabold text-slate-900 tracking-tight mb-4 max-w-3xl leading-[1.15]">
            Effortless document management for everyone.
          </h2>
          <p className="text-lg md:text-xl text-slate-500 max-w-2xl font-normal leading-relaxed">
            Convert, manage, and organize your files with professional-grade tools designed for speed, beauty, and absolute privacy.
          </p>
        </section>

        {/* Bento Grid layout */}
        <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-stretch">
          
          {/* Card 1: Active Task (Large Bento) */}
          <div 
            id="converter-card"
            onClick={() => onNavigate('converter')}
            className="md:col-span-8 group bg-white border border-slate-200/80 rounded-2xl p-6 md:p-8 flex flex-col justify-between gap-8 shadow-sm transition-all duration-300 hover:border-blue-500 hover:shadow-lg hover:-translate-y-1 cursor-pointer active:scale-[0.99]"
          >
            <div className="flex justify-between items-start">
              <div className="w-14 h-14 rounded-2xl bg-blue-50 flex items-center justify-center text-blue-600 shadow-sm transition-transform duration-300 group-hover:scale-110">
                <FileImage className="w-8 h-8" />
              </div>
              <div className="bg-blue-50 text-blue-700 px-4 py-1.5 rounded-full text-xs font-semibold tracking-wide uppercase">
                Most Popular
              </div>
            </div>

            <div>
              <h3 className="text-2xl font-bold text-slate-900 mb-2">Image to PDF</h3>
              <p className="text-slate-500 text-sm md:text-base leading-relaxed max-w-lg">
                Batch convert JPEG, PNG, and HEIC files into high-quality PDFs with custom orientation, alignment, and rotation logic. Runs entirely in your browser.
              </p>
            </div>

            <div className="flex items-center gap-2 text-blue-600 font-semibold text-sm group-hover:gap-4 transition-all">
              <span>Get Started</span>
              <ArrowRight className="w-4 h-4" />
            </div>
          </div>

          {/* Cards Stack (Coming Soon) */}
          <div className="md:col-span-4 flex flex-col gap-6">
            
            {/* Word to PDF Card */}
            <div className="bg-slate-50/80 border border-slate-200/40 rounded-2xl p-6 flex flex-col gap-4 opacity-75 relative overflow-hidden group">
              <div className="absolute top-3 right-3">
                <span className="bg-amber-100 text-amber-800 text-[10px] px-2.5 py-1 rounded-full font-bold uppercase tracking-wider">
                  Tahap Pengembangan
                </span>
              </div>
              <div className="w-11 h-11 rounded-xl bg-slate-200 flex items-center justify-center text-slate-500">
                <FileText className="w-6 h-6" />
              </div>
              <div>
                <h4 className="font-bold text-slate-800 text-base">Word to PDF</h4>
                <p className="text-slate-500 text-xs mt-1">Preserve font stylings and structural alignments in high resolution.</p>
              </div>
            </div>

            {/* Excel to PDF Card */}
            <div className="bg-slate-50/80 border border-slate-200/40 rounded-2xl p-6 flex flex-col gap-4 opacity-75 relative overflow-hidden group">
              <div className="absolute top-3 right-3">
                <span className="bg-amber-100 text-amber-800 text-[10px] px-2.5 py-1 rounded-full font-bold uppercase tracking-wider">
                  Tahap Pengembangan
                </span>
              </div>
              <div className="w-11 h-11 rounded-xl bg-slate-200 flex items-center justify-center text-slate-500">
                <TableProperties className="w-6 h-6" />
              </div>
              <div>
                <h4 className="font-bold text-slate-800 text-base">Excel to PDF</h4>
                <p className="text-slate-500 text-xs mt-1">Transform heavy complex spreadsheets dynamically into beautifully sized vector PDF tables.</p>
              </div>
            </div>

          </div>
        </div>

        {/* Local Security & Illustration Bento Segment */}
        <section className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-8 items-center bg-slate-950 text-white p-6 md:p-10 rounded-2xl relative overflow-hidden">
          <div className="absolute top-0 right-0 w-64 h-64 bg-slate-800/10 rounded-full blur-3xl" />
          <div className="absolute bottom-0 left-0 w-64 h-64 bg-blue-500/5 rounded-full blur-3xl" />

          <div className="order-2 md:order-1 relative z-10 flex flex-col gap-4">
            <div className="flex items-center gap-2">
              <div className="p-2 bg-blue-500/10 rounded-lg text-blue-400">
                <Shield className="w-5 h-5 animate-pulse" />
              </div>
              <span className="text-xs font-semibold text-blue-400 tracking-wider uppercase font-mono">
                100% Client-Side Safe
              </span>
            </div>
            <h4 className="text-2xl md:text-3xl font-bold tracking-tight text-white leading-tight">
              Your documents, protected.
            </h4>
            <p className="text-slate-400 text-sm md:text-base leading-relaxed">
              All file processing happens locally in your browser. We never load your sensitive inputs to remote servers. Speed, reliability, and security are native features.
            </p>
            <div>
              <button 
                onClick={() => setShowPrivacyModal(true)}
                className="bg-white hover:bg-slate-100 text-slate-900 font-semibold text-xs px-5 py-3 rounded-lg active:scale-95 transition-all w-fit cursor-pointer shadow-sm"
              >
                Learn About Privacy
              </button>
            </div>
          </div>

          <div className="order-1 md:order-2 flex justify-center relative">
            <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-transparent to-transparent z-10 md:hidden" />
            <img 
              alt="Secure workspace illustration representing reliable local utility" 
              className="rounded-xl shadow-lg border border-slate-800/80 w-full max-w-sm aspect-video object-cover relative z-0 transform hover:scale-[1.02] transition-transform duration-500" 
              src="https://lh3.googleusercontent.com/aida-public/AB6AXuCN242SWvH3Anr89r9ad095DWTjitFFRbNASYILY65GWYpf4kzWekcFQf11_UObInA8mSllzL3EzHK7xgMrM-5pDvMdlEFO1MW-YgSfl91kUDybth5LbF_Uwrx-ri5N7cSarkCqDcVoRNqaHAsxqpXql-ztVXpZTqp-CO-4VhHGRGfib8O6tobg5A1LHb3j0fyDjvqsfz-4ZAuC4lfylKpOFnVY7eutTo5fjZfWn0KLJc4X2sUilDx-yqBb6NwYIKDaM_pgT6oUdhyO"
            />
          </div>
        </section>

      </main>

      {/* Bottom Sticky Mobile Navigation */}
      <nav className="fixed bottom-0 left-0 w-full z-40 flex justify-around items-center h-16 pb-safe bg-white border-t border-slate-200/80 shadow-lg md:hidden">
        <button 
          onClick={() => onNavigate('dashboard')}
          className="flex flex-col items-center justify-center text-blue-600 font-bold transition-all p-2 cursor-pointer flex-1"
        >
          <FileCheck2 className="w-5 h-5 mb-0.5" />
          <span className="text-[10px] tracking-wide uppercase">Convert</span>
        </button>
        <button 
          onClick={() => onNavigate('history')}
          className="flex flex-col items-center justify-center text-slate-400 hover:text-blue-500 transition-all p-2 cursor-pointer flex-1"
        >
          <History className="w-5 h-5 mb-0.5" />
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

      {/* Sidebar Navigation */}
      {showSidebar && (
        <div className="fixed inset-0 z-[100] flex">
          <div 
            onClick={() => setShowSidebar(false)}
            className="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity duration-300 animate-fade-in"
          />
          <div className="relative bg-white w-72 max-w-[80vw] h-full flex flex-col justify-between py-6 px-4 shadow-2xl animate-[slideIn_0.3s_ease-out_forwards] border-r border-slate-100">
            <div>
              <div className="flex justify-between items-center mb-8 pb-4 border-b border-slate-100">
                <span className="font-bold text-slate-800 text-lg">Menu Utama</span>
                <button 
                  onClick={() => setShowSidebar(false)}
                  className="p-1 hover:bg-slate-100 rounded-lg cursor-pointer"
                >
                  <X className="w-5 h-5 text-slate-500" />
                </button>
              </div>

              <div className="flex flex-col gap-2">
                <button 
                  onClick={() => { onNavigate('dashboard'); setShowSidebar(false); }}
                  className="flex items-center gap-3 w-full p-3 rounded-xl hover:bg-slate-50 transition-colors text-left font-medium text-slate-700 hover:text-blue-600 hover:font-semibold"
                >
                  <FileCheck2 className="w-5 h-5 text-blue-500" />
                  <span>Dashboard</span>
                </button>
                <button 
                  onClick={() => { onNavigate('converter'); setShowSidebar(false); }}
                  className="flex items-center gap-3 w-full p-3 rounded-xl hover:bg-slate-50 transition-colors text-left font-medium text-slate-700 hover:text-blue-600 hover:font-semibold"
                >
                  <FileImage className="w-5 h-5 text-blue-500" />
                  <span>Image to PDF Tool</span>
                </button>
                <button 
                  onClick={() => { onNavigate('history'); setShowSidebar(false); }}
                  className="flex items-center gap-3 w-full p-3 rounded-xl hover:bg-slate-50 transition-colors text-left font-medium text-slate-700 hover:text-blue-600 hover:font-semibold"
                >
                  <History className="w-5 h-5 text-blue-500" />
                  <span>Riwayat Konversi</span>
                </button>
                <button 
                  onClick={() => { onNavigate('settings'); setShowSidebar(false); }}
                  className="flex items-center gap-3 w-full p-3 rounded-xl hover:bg-slate-50 transition-colors text-left font-medium text-slate-700 hover:text-blue-600 hover:font-semibold"
                >
                  <SettingsIcon className="w-5 h-5 text-blue-500" />
                  <span>Pengaturan</span>
                </button>
              </div>
            </div>

            <div className="border-t border-slate-100 pt-4 text-center">
              <span className="text-xs text-slate-400 block">VelaPDF v1.0.0</span>
              <span className="text-[10px] text-slate-400 block mt-1 font-mono">100% Safe client sandbox</span>
            </div>
          </div>
        </div>
      )}

      {/* Privacy modal */}
      {showPrivacyModal && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
          <div 
            onClick={() => setShowPrivacyModal(false)}
            className="absolute inset-0 bg-slate-900/50 backdrop-blur-sm"
          />
          <div className="relative bg-white w-full max-w-md rounded-2xl shadow-xl p-6 border border-slate-200">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-bold text-slate-900">Privacy Safeguard Info</h3>
              <button 
                onClick={() => setShowPrivacyModal(false)}
                className="p-1 hover:bg-slate-100 rounded-lg cursor-pointer"
              >
                <X className="w-5 h-5 text-slate-500" />
              </button>
            </div>
            
            <div className="flex flex-col gap-4 text-slate-600 text-sm leading-relaxed">
              <p>
                <strong>VelaPDF</strong> ensures absolute isolation for your documents. 
                Unlike conventional web utilities, your data does not upload to any computer system over the internet.
              </p>
              
              <ul className="list-disc pl-5 flex flex-col gap-2">
                <li>
                  <strong className="text-slate-800">Local Rendering:</strong> Image rendering and PDF packaging are calculated inside your browser using memory-safe WASM structures.
                </li>
                <li>
                  <strong className="text-slate-800">Zero Cloud Logs:</strong> We do not track document metadata, filename information, or user files.
                </li>
                <li>
                  <strong className="text-slate-800">Offline Ready:</strong> Once downloaded, the application works perfectly even with network connections fully severed.
                </li>
              </ul>
            </div>

            <div className="mt-6 flex justify-end">
              <button 
                onClick={() => setShowPrivacyModal(false)}
                className="bg-blue-600 hover:bg-blue-700 text-white font-semibold text-xs px-5 py-2.5 rounded-lg active:scale-95 transition-all text-center cursor-pointer"
              >
                Understand &amp; Close
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
