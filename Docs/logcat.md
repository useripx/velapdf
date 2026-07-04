2026-07-04 18:07:53.796 22901-22901 ImeTracker              com.njagakneai.velapdf               I  com.njagakneai.velapdf:ab691beb: onShown
2026-07-04 18:07:55.008 22901-22901 WindowOnBackDispatcher  com.njagakneai.velapdf               W  sendCancelIfRunning: isInProgress=falsecallback=android.view.ViewRootImpl$$ExternalSyntheticLambda20@c79e8f
2026-07-04 18:07:55.013 22901-22901 View                    com.njagakneai.velapdf               D  [Warning] assignParent to null: this = androidx.compose.ui.window.PopupLayout{8e8451c V.E...... ......I. 0,0-62,75 #1020002 android:id/content}
2026-07-04 18:07:55.015 22901-22901 BLASTBufferQueue        com.njagakneai.velapdf               D  [VRI[Jendela Pop-Up]#2](f:0,a:3) destructor()
2026-07-04 18:07:55.015 22901-22901 BufferQueueConsumer     com.njagakneai.velapdf               D  [VRI[Jendela Pop-Up]#2(BLAST Consumer)2](id:597500000002,api:0,p:-1,c:22901) disconnect
2026-07-04 18:07:55.026 22901-22901 InputTransport          com.njagakneai.velapdf               D  Destroy ARC handle: 0xb4000072788451a0
2026-07-04 18:07:59.429 22901-22901 WindowOnBackDispatcher  com.njagakneai.velapdf               W  sendCancelIfRunning: isInProgress=falsecallback=ImeCallback=ImeOnBackInvokedCallback@210517447 Callback=android.window.IOnBackInvokedCallback$Stub$Proxy@77d1bc2
2026-07-04 18:07:59.737 22901-22901 ImeTracker              com.njagakneai.velapdf               I  com.njagakneai.velapdf:93a3751e: onRequestHide at ORIGIN_CLIENT_HIDE_SOFT_INPUT reason HIDE_SOFT_INPUT_BY_INSETS_API
2026-07-04 18:07:59.739 22901-22901 ImeTracker              com.njagakneai.velapdf               I  com.njagakneai.velapdf:ea41b2d9: onHidden
2026-07-04 18:08:00.347 22901-22901 ImeTracker              com.njagakneai.velapdf               I  com.njagakneai.velapdf:94e5faba: onRequestHide at ORIGIN_CLIENT_HIDE_SOFT_INPUT reason HIDE_SOFT_INPUT_BY_INSETS_API
2026-07-04 18:08:00.347 22901-22901 ImeTracker              com.njagakneai.velapdf               I  com.njagakneai.velapdf:94e5faba: onCancelled at PHASE_CLIENT_APPLY_ANIMATION
2026-07-04 18:08:02.552 22901-23836 TranClassInfo           com.njagakneai.velapdf               D  instance successfully. com.transsion.hubcore.database.sqlite.TranSQLiteDatabaseImpl@b5e52fc from com.transsion.hubcore.database.sqlite.ITranSQLiteDatabase
2026-07-04 18:08:02.552 22901-23836 TranClassInfo           com.njagakneai.velapdf               D  instance successfully. com.transsion.hubcore.spdopts.others.implement.TranSQLiteDatabaseComponentImpl@f5a4d85 from com.transsion.hubcore.spdcomponent.ITranSQLiteDatabaseComponent
2026-07-04 18:08:02.762 22901-22901 Compatibil...geReporter com.njagakneai.velapdf               D  Compat change id reported: 160794467; UID 10657; state: ENABLED
2026-07-04 18:08:02.800 22901-22901 AndroidRuntime          com.njagakneai.velapdf               E  FATAL EXCEPTION: main (Fix with AI)
                                                                                                    Process: com.njagakneai.velapdf, PID: 22901
                                                                                                    android.os.FileUriExposedException: file:///storage/emulated/0/Documents/VelaPDF/172-Yogi%20Ario%20Pratama%20(1)_3 exposed beyond app through Intent.getData()
                                                                                                    	at android.os.StrictMode.onFileUriExposed(StrictMode.java:2245)
                                                                                                    	at android.net.Uri.checkFileUriExposed(Uri.java:2409)
                                                                                                    	at android.content.Intent.prepareToLeaveProcess(Intent.java:12130)
                                                                                                    	at android.content.Intent.prepareToLeaveProcess(Intent.java:12079)
                                                                                                    	at android.app.PendingIntent.getActivityAsUser(PendingIntent.java:547)
                                                                                                    	at android.app.PendingIntent.getActivity(PendingIntent.java:530)
                                                                                                    	at android.app.PendingIntent.getActivity(PendingIntent.java:494)
                                                                                                    	at com.njagakneai.velapdf.utils.NotificationHelper.showPdfCompleteNotification(NotificationHelper.kt:66)
                                                                                                    	at com.njagakneai.velapdf.ui.screen.PdfToImageScreenKt$PdfToImageScreen$2$1.invokeSuspend(PdfToImageScreen.kt:126)
                                                                                                    	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
                                                                                                    	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:101)
                                                                                                    	at androidx.compose.ui.platform.AndroidUiDispatcher.performTrampolineDispatch(AndroidUiDispatcher.android.kt:79)
                                                                                                    	at androidx.compose.ui.platform.AndroidUiDispatcher.access$performTrampolineDispatch(AndroidUiDispatcher.android.kt:41)
                                                                                                    	at androidx.compose.ui.platform.AndroidUiDispatcher$dispatchCallback$1.run(AndroidUiDispatcher.android.kt:57)
                                                                                                    	at android.os.Handler.handleCallback(Handler.java:958)
                                                                                                    	at android.os.Handler.dispatchMessage(Handler.java:99)
                                                                                                    	at android.os.Looper.loopOnce(Looper.java:243)
                                                                                                    	at android.os.Looper.loop(Looper.java:338)
                                                                                                    	at android.app.ActivityThread.main(ActivityThread.java:8470)
                                                                                                    	at java.lang.reflect.Method.invoke(Native Method)
                                                                                                    	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:600)
                                                                                                    	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1064)
                                                                                                    	Suppressed: kotlinx.coroutines.internal.DiagnosticCoroutineContextException: [androidx.compose.ui.platform.MotionDurationScaleImpl@addeade, androidx.compose.runtime.BroadcastFrameClock@3c0d4bf, androidx.compose.runtime.LaunchedEffectImpl@819a83d, StandaloneCoroutine{Cancelling}@4a6f78c, AndroidUiDispatcher@ccc3ed5]
2026-07-04 18:08:02.800 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at android.os.StrictMode.onFileUriExposed(StrictMode.java:2245)
2026-07-04 18:08:02.800 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at android.net.Uri.checkFileUriExposed(Uri.java:2409)
2026-07-04 18:08:02.800 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at android.content.Intent.prepareToLeaveProcess(Intent.java:12130)
2026-07-04 18:08:02.800 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at android.content.Intent.prepareToLeaveProcess(Intent.java:12079)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at android.app.PendingIntent.getActivityAsUser(PendingIntent.java:547)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at android.app.PendingIntent.getActivity(PendingIntent.java:530)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at android.app.PendingIntent.getActivity(PendingIntent.java:494)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at com.njagakneai.velapdf.utils.NotificationHelper.showPdfCompleteNotification(NotificationHelper.kt:66)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at com.njagakneai.velapdf.ui.screen.PdfToImageScreenKt$PdfToImageScreen$2$1.invokeSuspend(PdfToImageScreen.kt:126)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:101)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at androidx.compose.ui.platform.AndroidUiDispatcher.performTrampolineDispatch(AndroidUiDispatcher.android.kt:79)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at androidx.compose.ui.platform.AndroidUiDispatcher.access$performTrampolineDispatch(AndroidUiDispatcher.android.kt:41)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at androidx.compose.ui.platform.AndroidUiDispatcher$dispatchCallback$1.run(AndroidUiDispatcher.android.kt:57)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at android.os.Handler.handleCallback(Handler.java:958)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at android.os.Handler.dispatchMessage(Handler.java:99)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at android.os.Looper.loopOnce(Looper.java:243)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at android.os.Looper.loop(Looper.java:338)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at android.app.ActivityThread.main(ActivityThread.java:8470)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at java.lang.reflect.Method.invoke(Native Method)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:600)
2026-07-04 18:08:02.801 22901-22901 ExceptionHandle         com.njagakneai.velapdf               I  at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1064)
2026-07-04 18:08:02.840 22901-22901 Process                 com.njagakneai.velapdf               I  Sending signal. PID: 22901 SIG: 9
---------------------------- PROCESS ENDED (22901) for package com.njagakneai.velapdf ----------------------------