@ECHO OFF

cd /d %~dp0

SET session=amerep-bot-amethyst

IF /i "%1"=="start" ( 
  pm2 start src/app.mjs -n %session%
) ELSE IF /i "%1"=="stop" ( 
  pm2 stop %session%
) ELSE IF /i "%1"=="restart" ( 
  pm2 restart %session%
) ELSE IF /i "%1"=="logs" ( 
  pm2 logs %session%
)
