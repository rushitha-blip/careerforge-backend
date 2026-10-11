@echo off
setlocal
title CareerForge AI - Backend GitHub Push Utility
echo =======================================================================
echo   CareerForge AI - Java Spring Boot Backend GitHub Push
echo =======================================================================
echo.
echo Step 1: Go to GitHub (https://github.com/new) and create a NEW repository:
echo         Repository name: careerforge-backend (or any name you prefer)
echo         Make sure it is PUBLIC. Do NOT check "Add a README file".
echo.
echo Step 2: Copy the repository URL (e.g., https://github.com/your-username/careerforge-backend.git)
echo.
set /p REPO_URL="Paste your Backend GitHub Repository URL here: "

if "%REPO_URL%"=="" (
    echo.
    echo [ERROR] No URL provided. Exiting...
    pause
    exit /b 1
)

echo.
echo [1/4] Initializing Git repository...
git init
git branch -M main

echo [2/4] Staging all backend files...
git add .

echo [3/4] Committing codebase...
git commit -m "feat: complete Java 21 Spring Boot backend with Spring Security, OpenPDF, JPA, and JUnit 5 test suite"

echo [4/4] Pushing to %REPO_URL%...
git remote remove origin 2>nul
git remote add origin %REPO_URL%
git push -u origin main --force

echo.
if %ERRORLEVEL% EQU 0 (
    echo =======================================================================
    echo   SUCCESS! Backend repository pushed to GitHub successfully!
    echo   Your Backend URL: %REPO_URL%
    echo =======================================================================
) else (
    echo =======================================================================
    echo   Push failed. Please check your internet connection or GitHub login.
    echo =======================================================================
)
echo.
pause
