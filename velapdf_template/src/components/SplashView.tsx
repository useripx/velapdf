import React, { useEffect, useState } from 'react';

interface SplashViewProps {
  onComplete: () => void;
}

export default function SplashView({ onComplete }: SplashViewProps) {
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    const duration = 2000; // 2 seconds
    const intervalTime = 40;
    const increment = 100 / (duration / intervalTime);

    const timer = setInterval(() => {
      setProgress((prev) => {
        const next = prev + increment;
        if (next >= 100) {
          clearInterval(timer);
          setTimeout(() => {
            onComplete();
          }, 400); // Small pause for UX satisfaction
          return 100;
        }
        return next;
      });
    }, intervalTime);

    return () => clearInterval(timer);
  }, [onComplete]);

  return (
    <div className="flex items-center justify-center min-h-screen bg-[#f7f9fb] select-none">
      <main className="relative w-full max-w-md mx-auto flex flex-col items-center justify-between h-[80vh] px-6">
        {/* Top Spacer */}
        <div className="h-16" />

        {/* Center Content: Logo and Brand */}
        <div className="flex flex-col items-center text-center animate-[fadeIn_0.8s_ease-out_forwards]">
          <div className="mb-6 relative transition-transform duration-700 hover:scale-105 active:scale-95 cursor-pointer">
            {/* Ambient circular pulse glow behind the logo */}
            <div className="absolute inset-0 bg-blue-500/5 rounded-full scale-125 blur-xl animate-pulse" />
            
            {/* Dynamic custom designed SVG Document + Checkmark brand mark */}
            <svg 
              width="150" 
              height="150" 
              viewBox="0 0 512 512" 
              className="drop-shadow-lg"
              aria-label="VelaPDF Logo"
            >
              <defs>
                <linearGradient id="checkmarkGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stopColor="#2563eb" />
                  <stop offset="100%" stopColor="#1d4ed8" />
                </linearGradient>
              </defs>
              {/* Document Base */}
              <rect x="80" y="40" width="352" height="432" rx="42" fill="#0f172a" />
              {/* Document Text Lines */}
              <rect x="160" y="160" width="192" height="36" rx="18" fill="#ffffff" />
              <rect x="160" y="250" width="192" height="36" rx="18" fill="#ffffff" />
              {/* Custom Overlaid Checkmark Accent */}
              <path 
                d="M170 340 L250 420 L450 210" 
                fill="none" 
                stroke="url(#checkmarkGrad)" 
                strokeWidth="52" 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                className="animate-[dash_1.5s_ease-in-out_forwards]"
                style={{
                  strokeDasharray: 600,
                  strokeDashoffset: progress === 100 ? 0 : 500 - (progress * 5),
                }}
              />
            </svg>
          </div>

          <h1 className="text-4xl font-bold text-slate-900 tracking-tight font-sans">
            VelaPDF
          </h1>
          <p className="text-base text-slate-500 mt-2 font-medium opacity-80 max-w-[280px]">
            High-performance document tools for the modern professional.
          </p>
        </div>

        {/* Bottom Section: Progress Bar, Status, & Subtext */}
        <div className="w-full flex flex-col items-center">
          <div className="w-48 bg-slate-200 rounded-full h-1 overflow-hidden mb-4">
            <div 
              className="h-full bg-blue-600 transition-all duration-300 ease-out"
              style={{ width: `${progress}%` }}
            />
          </div>
          <span className="text-xs font-semibold text-slate-400 tracking-widest uppercase">
            PREPARING ENVIRONMENT
          </span>

          <div className="mt-12 opacity-50">
            <p className="text-xs font-mono text-slate-400 font-medium">
              Powered by React &amp; Tailwind CSS
            </p>
          </div>
        </div>
      </main>
    </div>
  );
}
