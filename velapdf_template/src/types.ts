export type AppView = 'splash' | 'dashboard' | 'converter' | 'success' | 'history' | 'settings';

export interface HistoryItem {
  id: string;
  filename: string;
  fileSize: string;
  pagesCount: number;
  dateStr: string;
  timestamp: number;
  thumbnailDataUrl?: string; // Cache low-res thumbnail
  pdfDataUrl: string; // The generated base64 blob to enable re-downloading!
}

export interface SelectedImage {
  id: string;
  file: File;
  previewUrl: string;
  rotation: number; // 0, 90, 180, 270 degrees
}
