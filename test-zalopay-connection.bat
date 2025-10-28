@echo off
echo ========================================
echo Testing ZaloPay Connection
echo ========================================
echo.

echo 1. Testing DNS resolution...
nslookup sb-openapi.zalopay.vn
echo.

echo 2. Testing ping...
ping -n 4 sb-openapi.zalopay.vn
echo.

echo 3. Testing HTTPS connection with curl...
curl -v https://sb-openapi.zalopay.vn/v2/create
echo.

echo ========================================
echo Test completed!
echo ========================================
pause
