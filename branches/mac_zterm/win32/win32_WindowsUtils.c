#include <assert.h>

#include "win32_WindowsUtils.h"

#include "jni_md.h"
#include "jawt_md.h"

JNIEXPORT void JNICALL Java_win32_WindowsUtils_flash
(JNIEnv *env, jobject f, jobject component, jboolean bool)
{
	JAWT awt;
	JAWT_DrawingSurface* ds;
	JAWT_DrawingSurfaceInfo* dsi;
	JAWT_Win32DrawingSurfaceInfo* dsi_win;
	jboolean result;

	jint lock;

	// Get the AWT
	awt.version = JAWT_VERSION_1_3;
	result = JAWT_GetAWT(env, &awt);
	assert(result != JNI_FALSE);
	// Get the drawing surface
	ds = awt.GetDrawingSurface(env, component);
	if(ds == NULL)
		return;
	// Lock the drawing surface
	lock = ds->Lock(ds);
	assert((lock & JAWT_LOCK_ERROR) == 0);

	// Get the drawing surface info
	dsi = ds->GetDrawingSurfaceInfo(ds);

	// Get the platform-specific drawing info
	dsi_win = (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;

	FlashWindow(dsi_win->hwnd,bool);

	// Free the drawing surface info
	ds->FreeDrawingSurfaceInfo(dsi);
	// Unlock the drawing surface
	ds->Unlock(ds);
	// Free the drawing surface
	awt.FreeDrawingSurface(ds);

}
