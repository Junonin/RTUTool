; 脚本由 Inno Setup 脚本向导 生成！
; 有关创建 Inno Setup 脚本文件的详细资料请查阅帮助文档！

#define MyAppName "ydtools"
#define MyAppVersion "1.0.0"
#define MyAppPublisher "广州远动信息技术有限公司"
#define MyAppURL "http://www.gdremote.com/"
#define MyAppExeName "ydtools.exe"

[Setup]
; 注: AppId的值为单独标识该应用程序。
; 不要为其他安装程序使用相同的AppId值。
; (生成新的GUID，点击 工具|在IDE中生成GUID。)
AppId={{0FEE9BF6-7067-4230-8285-5309E3CAFBC0}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
OutputDir=C:\Users\JUNO\Desktop
OutputBaseFilename=ydtools
SetupIconFile=C:\Users\JUNO\Desktop\tools\tool.ico
Compression=lzma
SolidCompression=yes

[Languages]
Name: "chinesesimp"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; OnlyBelowVersion: 0,8.1
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; 

[Files]
Source: "C:\Users\JUNO\Desktop\tools\ydtool_32.exe"; DestDir: "{app}"; DestName: "ydtools.exe"; Flags: ignoreversion ;
Source: "C:\Users\JUNO\Desktop\tools\jre32\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs;
; 注意: 不要在任何共享系统文件上使用“Flags: ignoreversion”
;Source: "C:\Users\JUNO\Desktop\tools\ydtool_64.exe"; DestDir: "{app}"; DestName: "ydtools.exe"; Flags: restartreplace ignoreversion regserver 64bit;
;Source: "C:\Users\JUNO\Desktop\tools\jre64\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs; 


[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{group}\{cm:ProgramOnTheWeb,{#MyAppName}}"; Filename: "{#MyAppURL}"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon;
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: quicklaunchicon;
;生成对应系统快捷键{userdesktop} 表示桌面生成一个tool64.exe图标  Filename表示对应exe文件，WorkingDir工作目录;Name:"{userdesktop}\ydtools.exe"; Filename:"{app}\ydtools.exe"; WorkingDir:"{app}"; 

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent;

