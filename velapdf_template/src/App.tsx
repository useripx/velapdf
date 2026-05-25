import React, { useState, useEffect } from 'react';
import { AppView, HistoryItem } from './types';
import SplashView from './components/SplashView';
import DashboardView from './components/DashboardView';
import ConverterView from './components/ConverterView';
import SuccessView from './components/SuccessView';
import HistoryListView from './components/HistoryListView';
import SettingsView from './components/SettingsView';

const STORAGE_KEY = 'velapdf_conversion_history';
const SETTING_PAGE_KEY = 'velapdf_pref_page_size';

export default function App() {
  const [currentView, setCurrentView] = useState<AppView>('splash');
  const [historyList, setHistoryList] = useState<HistoryItem[]>([]);
  const [pageSize, setPageSize] = useState<string>('AUTO');
  const [latestConversion, setLatestConversion] = useState<HistoryItem | null>(null);

  // Load persistent configurations and past uploads
  useEffect(() => {
    try {
      const storedHistory = localStorage.getItem(STORAGE_KEY);
      if (storedHistory) {
        setHistoryList(JSON.parse(storedHistory));
      }

      const storedPageSize = localStorage.getItem(SETTING_PAGE_KEY);
      if (storedPageSize) {
        setPageSize(storedPageSize);
      }
    } catch (err) {
      console.error('Failed to load local store history cache', err);
    }
  }, []);

  // Save history change to local storage
  const saveHistoryToStorage = (updatedList: HistoryItem[]) => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(updatedList));
    } catch (err) {
      console.error('Failed to preserve local store history', err);
    }
  };

  // Handle a new successful conversion
  const handleConversionSuccess = (item: HistoryItem) => {
    const updated = [item, ...historyList];
    setHistoryList(updated);
    saveHistoryToStorage(updated);
    setLatestConversion(item);
    setCurrentView('success');
  };

  // Delete individual history item
  const handleDeleteHistoryItem = (id: string) => {
    const updated = historyList.filter((item) => item.id !== id);
    setHistoryList(updated);
    saveHistoryToStorage(updated);
  };

  // Wipe all local history
  const handleClearHistory = () => {
    setHistoryList([]);
    localStorage.removeItem(STORAGE_KEY);
  };

  // Handle default page format size change
  const handlePageSizeChange = (size: string) => {
    setPageSize(size);
    try {
      localStorage.setItem(SETTING_PAGE_KEY, size);
    } catch (err) {
      console.error(err);
    }
  };

  // Render sub-windows depending on active view state
  const renderActiveView = () => {
    switch (currentView) {
      case 'splash':
        return (
          <SplashView 
            onComplete={() => setCurrentView('dashboard')} 
          />
        );
      case 'dashboard':
        return (
          <DashboardView 
            onNavigate={setCurrentView} 
          />
        );
      case 'converter':
        return (
          <ConverterView 
            onNavigate={setCurrentView}
            onConversionSuccess={handleConversionSuccess}
            pageSize={pageSize}
          />
        );
      case 'success':
        return (
          <SuccessView 
            item={latestConversion}
            onNavigate={setCurrentView}
          />
        );
      case 'history':
        return (
          <HistoryListView 
            history={historyList}
            onNavigate={setCurrentView}
            onDeleteHistoryItem={handleDeleteHistoryItem}
            onClearHistory={handleClearHistory}
          />
        );
      case 'settings':
        return (
          <SettingsView 
            onNavigate={setCurrentView}
            pageSize={pageSize}
            onPageSizeChange={handlePageSizeChange}
            historyCount={historyList.length}
            onWipeAllHistory={handleClearHistory}
          />
        );
      default:
        return <DashboardView onNavigate={setCurrentView} />;
    }
  };

  return (
    <div className="min-h-screen bg-[#f7f9fb] antialiased">
      {renderActiveView()}
    </div>
  );
}
