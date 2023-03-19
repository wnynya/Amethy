@ECHO ON
CHCP 65001
SET SESSION=amerep-bot-amethyst
SET MESSAGE=명령어 입력 후 엔터!
CD /d %~dp0

:MAIN
CLS
echo.
echo KEYS:
echo   start, stop, restart, logs
echo.
echo %MESSAGE%
echo.
SET /p KEY="> "

IF /i "%KEY%"=="start" ( 
  GOTO START
) ELSE IF /i "%KEY%"=="stop" ( 
  cmd.exe /c pm2 stop %SESSION%
  GOTO MAIN
) ELSE IF /i "%KEY%"=="restart" ( 
  pm2 restart %SESSION%
  GOTO MAIN
) ELSE IF /i "%KEY%"=="logs" ( 
  pm2 logs %SESSION%
  GOTO MAIN
) ELSE (
  SET MESSAGE=알 수 없는 명령어입니다.
  GOTO MAIN
)

:START
pm2 start src/app.mjs -n %SESSION%
pause
GOTO MAIN