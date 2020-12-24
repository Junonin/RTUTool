; �ű��� Inno Setup �ű��� ���ɣ�
; �йش��� Inno Setup �ű��ļ�����ϸ��������İ����ĵ���

#define MyAppName "ydtools"
#define MyAppVersion "1.0.0"
#define MyAppPublisher "����Զ����Ϣ�������޹�˾"
#define MyAppURL "http://www.gdremote.com/"
#define MyAppExeName "ydtools.exe"

[Setup]
; ע: AppId��ֵΪ������ʶ��Ӧ�ó���
; ��ҪΪ������װ����ʹ����ͬ��AppIdֵ��
; (�����µ�GUID����� ����|��IDE������GUID��)
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
; ע��: ��Ҫ���κι���ϵͳ�ļ���ʹ�á�Flags: ignoreversion��
;Source: "C:\Users\JUNO\Desktop\tools\ydtool_64.exe"; DestDir: "{app}"; DestName: "ydtools.exe"; Flags: restartreplace ignoreversion regserver 64bit;
;Source: "C:\Users\JUNO\Desktop\tools\jre64\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs; 


[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{group}\{cm:ProgramOnTheWeb,{#MyAppName}}"; Filename: "{#MyAppURL}"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon;
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: quicklaunchicon;
;���ɶ�Ӧϵͳ��ݼ�{userdesktop} ��ʾ��������һ��tool64.exeͼ��  Filename��ʾ��Ӧexe�ļ���WorkingDir����Ŀ¼;Name:"{userdesktop}\ydtools.exe"; Filename:"{app}\ydtools.exe"; WorkingDir:"{app}"; 

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent;

