@echo off
echo ========================================
echo Testing Deep Link on Android Emulator
echo ========================================
echo.

echo Make sure:
echo 1. Android Emulator is running
echo 2. React Native app is installed
echo 3. AndroidManifest.xml is configured
echo.

set /p APP_PACKAGE="Enter your app package name (e.g., com.cinemaapp): "
set /p BOOKING_ID="Enter booking ID to test (e.g., 43): "

echo.
echo Testing deep link: cinemaapp://payment/result?bookingId=%BOOKING_ID%
echo.

adb shell am start -W -a android.intent.action.VIEW -d "cinemaapp://payment/result?bookingId=%BOOKING_ID%" %APP_PACKAGE%

echo.
echo ========================================
echo Test completed!
echo ========================================
echo.
echo If app opened successfully, deep link is working!
echo If not, check:
echo - AndroidManifest.xml configuration
echo - App is installed on emulator
echo - Package name is correct
echo.
pause
