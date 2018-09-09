/// ------------------------------------------------------------------
/// Copyright (c) 1996, 2004 Vincent Risi in Association 
///                          with Barone Budge and Dominick 
/// All rights reserved. 
/// This program and the accompanying materials are made available 
/// under the terms of the Common Public License v1.0 
/// which accompanies this distribution and is available at 
/// http://www.eclipse.org/legal/cpl-v10.html 
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// $Date: 2004/11/17 08:50:32 $
/// $Revision: 411.2 $ // YMM.Revision
/// ------------------------------------------------------------------

using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Data;
using System.Text;
using System.IO;
using bbd.jportal;
using System.Reflection;
using System.Diagnostics;
using ICSharpCode.TextEditor;
using ICSharpCode.TextEditor.Document;

namespace Bbd.AnyDB
{
	/// <summary>
	/// Summary description for Form1.
	/// </summary>
  public class IDEForm : System.Windows.Forms.Form
  {
    #region Windows Form Designer generated code
    private System.ComponentModel.IContainer components;
    private System.Windows.Forms.Button sourceDirectoryButton;
    private System.Windows.Forms.Button targetDirectoryButton;
    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.Label label2;
    private System.Windows.Forms.Label label3;
    private System.Windows.Forms.Label label4;
    private System.Windows.Forms.Label label5;
    private System.Windows.Forms.Label label6;
    private System.Windows.Forms.Label label7;
    private System.Windows.Forms.Label label8;
    private System.Windows.Forms.Label label9;
    private System.Windows.Forms.ListBox sourceList;
    private System.Windows.Forms.ListBox targetList;
    private System.Windows.Forms.MenuItem saveAsFileEPM;
    private System.Windows.Forms.MenuItem saveAsFileMenu;
    private System.Windows.Forms.MenuItem saveAsProjectMenu;
    private System.Windows.Forms.MenuItem saveFileEPM;
    private System.Windows.Forms.MenuItem saveFileMenu;
    private System.Windows.Forms.MenuItem saveProjectMenu;
    private System.Windows.Forms.MenuItem selectAllMenu;
    private System.Windows.Forms.MenuItem sep0FileMenu;
    private System.Windows.Forms.MenuItem sep1EPM;
    private System.Windows.Forms.MenuItem sep1EditMenu;
    private System.Windows.Forms.MenuItem sep1FileMenu;
    private System.Windows.Forms.MenuItem sep2EPM;
    private System.Windows.Forms.MenuItem sep2EditMenu;
    private System.Windows.Forms.MenuItem sep2FileMenu;
    private System.Windows.Forms.MenuItem sep3EPM;
    private System.Windows.Forms.MenuItem sep3EditMenu;
    private System.Windows.Forms.MenuItem sep3FileMenu;
    private System.Windows.Forms.MenuItem sep4EditMenu;
    private System.Windows.Forms.MenuItem sep4Filemenu;
    private System.Windows.Forms.MenuItem undoEPM;
    private System.Windows.Forms.MenuItem undoMenu;
    private System.Windows.Forms.Splitter sourceTargetSplitter;
    private System.Windows.Forms.StatusBar statusBar;
    private System.Windows.Forms.TabControl editTab;
    private System.Windows.Forms.TabControl viewTab;
    private System.Windows.Forms.TabPage viewPage;
    private System.Windows.Forms.TextBox server;
    private System.Windows.Forms.TextBox userId;
    private System.Windows.Forms.ToolBar viewToolbar;
    private System.Windows.Forms.ToolBarButton saveAsFile;
    private System.Windows.Forms.ToolBarButton saveAsProject;
    private System.Windows.Forms.ToolBarButton saveFile;
    private System.Windows.Forms.ToolBarButton saveProject;
    private System.Windows.Forms.ToolBarButton sep1Edit;
    private System.Windows.Forms.ToolBarButton sep1Log;
    private System.Windows.Forms.ToolBarButton sep1View;
    private System.Windows.Forms.ToolBarButton sep2Edit;
    private System.Windows.Forms.ToolBarButton sep2View;
    private System.Windows.Forms.ToolBarButton sep3Edit;
    private System.Windows.Forms.ToolBarButton sep3View;
    private System.Windows.Forms.ToolBarButton sep4Edit;
    private System.Windows.Forms.ToolBarButton sep4View;
    private System.Windows.Forms.ToolBarButton sep5Edit;
    private System.Windows.Forms.ToolBarButton undoEdit;
    private System.Windows.Forms.StatusBarPanel editPositionStatus;
    private System.Windows.Forms.StatusBarPanel editModeStatus;
    private System.Windows.Forms.StatusBarPanel editMessageStatus;
    private System.Windows.Forms.MenuItem fileMenu;
    private System.Windows.Forms.MenuItem closeFileMenu;
    private System.Windows.Forms.MenuItem closeViewMenu;
    private System.Windows.Forms.MenuItem exitMenu;
    private System.Windows.Forms.MenuItem editMenu;
    private System.Windows.Forms.MenuItem cutMenu;
    private System.Windows.Forms.MenuItem copyMenu;
    private System.Windows.Forms.MenuItem deleteMenu;
    private System.Windows.Forms.MenuItem compileMenu;
    private System.Windows.Forms.MenuItem compileOutOfDateMenu;
    private System.Windows.Forms.MenuItem compileAllMenu;
    private System.Windows.Forms.MenuItem aboutMenu;
    private System.Windows.Forms.ImageList images;
    private System.Windows.Forms.Panel displaysPanel;
    private System.Windows.Forms.TabPage editPage;
    private System.Windows.Forms.ContextMenu editPopupMenu;
    private System.Windows.Forms.MenuItem closeFileEPM;
    private System.Windows.Forms.MenuItem cutEPM;
    private System.Windows.Forms.MenuItem copyEPM;
    private System.Windows.Forms.ToolBar editToolbar;
    private System.Windows.Forms.ToolBarButton compileEdit;
    private System.Windows.Forms.ToolBarButton buildEdit;
    private System.Windows.Forms.ToolBarButton closeFileEdit;
    private System.Windows.Forms.ToolBarButton cutEdit;
    private System.Windows.Forms.ToolBarButton copyEdit;
    private System.Windows.Forms.ToolBarButton compileView;
    private System.Windows.Forms.ToolBarButton closePageView;
    private System.Windows.Forms.ToolBarButton copyView;
    private System.Windows.Forms.TextBox configDirectory;
    private System.Windows.Forms.TextBox database;
    private System.Windows.Forms.ToolBarButton clearLog;
    private System.Windows.Forms.StatusBarPanel lastLogMessage;
    private System.Windows.Forms.MainMenu ideMenu;
    private System.Windows.Forms.MenuItem findAndReplaceMenu;
    private System.Windows.Forms.MenuItem findMenu;
    private System.Windows.Forms.MenuItem goToMenu;
    private System.Windows.Forms.MenuItem helpMenu;
    private System.Windows.Forms.Panel listsPanel;
    private System.Windows.Forms.ToolBar fileToolbar;
    private System.Windows.Forms.Splitter mainSplitter;
    private System.Windows.Forms.TabControl mainTab;
    private System.Windows.Forms.MenuItem findEPM;
    private System.Windows.Forms.MenuItem goToEPM;
    private System.Windows.Forms.ToolBarButton makeEdit;
    private System.Windows.Forms.ToolBarButton findEdit;
    private System.Windows.Forms.ToolBarButton findView;
    private System.Windows.Forms.TextBox initialTargetDirectory;
    private System.Windows.Forms.TextBox initialSourceDirectory;
    private System.Windows.Forms.TabPage generatorsPage;
    private System.Windows.Forms.Panel generatorsList;
    private System.Windows.Forms.ToolBar generatorToolBar;
    private System.Windows.Forms.TabPage logPage;
    private System.Windows.Forms.ToolBar logToolBar;
    private System.Windows.Forms.MenuItem newFileMenu;
    private System.Windows.Forms.MenuItem openFileMenu;
    private System.Windows.Forms.MenuItem removeFileMenu;
    private System.Windows.Forms.MenuItem openProjectMenu;
    private System.Windows.Forms.MenuItem newProjectMenu;
    private System.Windows.Forms.MenuItem recentProjectsMenu;
    private System.Windows.Forms.MenuItem printSetupMenu;
    private System.Windows.Forms.MenuItem printMenu;
    private System.Windows.Forms.MenuItem redoMenu;
    private System.Windows.Forms.MenuItem pasteMenu;
    private System.Windows.Forms.MenuItem replaceMenu;
    private System.Windows.Forms.ToolBar projectToolbar;
    private System.Windows.Forms.ToolBarButton newProject;
    private System.Windows.Forms.ToolBarButton openProject;
    private System.Windows.Forms.ToolBarButton removeSource;
    private System.Windows.Forms.ToolBarButton newFile;
    private System.Windows.Forms.ToolBarButton openFile;
    private System.Windows.Forms.MenuItem redoEPM;
    private System.Windows.Forms.MenuItem pasteEPM;
    private System.Windows.Forms.MenuItem replaceEPM;
    private System.Windows.Forms.ToolBarButton replaceEdit;
    private System.Windows.Forms.ToolBarButton pasteEdit;
    private System.Windows.Forms.ToolBarButton redoEdit;
    private System.Windows.Forms.ToolBarButton printEdit;
    private System.Windows.Forms.ToolBarButton printSetupEdit;
    private System.Windows.Forms.ToolBarButton printView;
    private System.Windows.Forms.ToolBarButton printSetupView;
    private System.Windows.Forms.TabPage optionTab;
    private System.Windows.Forms.Panel optionsList;
    private System.Windows.Forms.TextBox output;
    private System.Windows.Forms.TextBox password;
    private System.Windows.Forms.TextBox package;
    private System.Windows.Forms.ToolBar optionToolBar;
    private System.Windows.Forms.ToolBarButton printLog;
    private System.Windows.Forms.ToolBarButton printSetupLog;
    private System.Windows.Forms.ContextMenu viewerPopupMenu;
    private System.Windows.Forms.MenuItem copyVPM;
    private System.Windows.Forms.MenuItem menuItem3;
    private System.Windows.Forms.MenuItem findVPM;
    private System.Windows.Forms.MenuItem goToVPM;
    private System.Windows.Forms.MenuItem menuItem7;
    private System.Windows.Forms.MenuItem PrintVPM;
    private System.Windows.Forms.MenuItem printSetupVPM;
    private System.Windows.Forms.ContextMenu logPopupMenu;
    private System.Windows.Forms.MenuItem clearLPM;
    private System.Windows.Forms.MenuItem menuItem2;
    private System.Windows.Forms.MenuItem copyLPM;
    private System.Windows.Forms.MenuItem menuItem4;
    private System.Windows.Forms.MenuItem printLPM;
    private System.Windows.Forms.MenuItem printSetupLPM;
    private System.Windows.Forms.PrintDialog printDialog;
    private System.Drawing.Printing.PrintDocument printDocument;
    private System.Windows.Forms.MenuItem sep4EPM;
    private System.Windows.Forms.MenuItem printEPM;
    private System.Windows.Forms.MenuItem printSetupEPM;
    private System.Windows.Forms.GroupBox logLevelGroupBox;
    private System.Windows.Forms.Label label10;
    private System.Windows.Forms.Label label11;
    private System.Windows.Forms.Label label12;
    private System.Windows.Forms.Label label13;
    private System.Windows.Forms.Label label14;
    private System.Windows.Forms.TrackBar logLevelTrackBar;
    private System.Windows.Forms.MenuItem helpContentsMenuItem;
    private System.Windows.Forms.HelpProvider helpProvider;
    private System.Windows.Forms.Panel logPanel;
    private System.Windows.Forms.Panel addinPanel;
    private System.Windows.Forms.Splitter addinSplitter;
    private System.Windows.Forms.ToolTip toolTips;

    public IDEForm()
    {
      //
      // Required for Windows Form Designer support
      //
      InitializeComponent();

      //
      // TODO: Add any constructor code after InitializeComponent call
      //
    }
    /// <summary>
    /// Clean up any resources being used.
    /// </summary>
    protected override void Dispose( bool disposing )
    {
      if( disposing )
      {
        if (components != null)
        {
          components.Dispose();
        }
      }
      base.Dispose( disposing );
    }
    /// <summary>
    /// Required method for Designer support - do not modify
    /// the contents of this method with the code editor.
    /// </summary>
    private void InitializeComponent()
    {
      this.components = new System.ComponentModel.Container();
      System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(IDEForm));
      System.Configuration.AppSettingsReader configurationAppSettings = new System.Configuration.AppSettingsReader();
      this.statusBar = new System.Windows.Forms.StatusBar();
      this.editPositionStatus = new System.Windows.Forms.StatusBarPanel();
      this.editModeStatus = new System.Windows.Forms.StatusBarPanel();
      this.editMessageStatus = new System.Windows.Forms.StatusBarPanel();
      this.lastLogMessage = new System.Windows.Forms.StatusBarPanel();
      this.ideMenu = new System.Windows.Forms.MainMenu();
      this.fileMenu = new System.Windows.Forms.MenuItem();
      this.newFileMenu = new System.Windows.Forms.MenuItem();
      this.openFileMenu = new System.Windows.Forms.MenuItem();
      this.saveFileMenu = new System.Windows.Forms.MenuItem();
      this.saveAsFileMenu = new System.Windows.Forms.MenuItem();
      this.removeFileMenu = new System.Windows.Forms.MenuItem();
      this.sep0FileMenu = new System.Windows.Forms.MenuItem();
      this.closeFileMenu = new System.Windows.Forms.MenuItem();
      this.closeViewMenu = new System.Windows.Forms.MenuItem();
      this.sep1FileMenu = new System.Windows.Forms.MenuItem();
      this.openProjectMenu = new System.Windows.Forms.MenuItem();
      this.newProjectMenu = new System.Windows.Forms.MenuItem();
      this.saveProjectMenu = new System.Windows.Forms.MenuItem();
      this.saveAsProjectMenu = new System.Windows.Forms.MenuItem();
      this.sep2FileMenu = new System.Windows.Forms.MenuItem();
      this.recentProjectsMenu = new System.Windows.Forms.MenuItem();
      this.sep3FileMenu = new System.Windows.Forms.MenuItem();
      this.printSetupMenu = new System.Windows.Forms.MenuItem();
      this.printMenu = new System.Windows.Forms.MenuItem();
      this.sep4Filemenu = new System.Windows.Forms.MenuItem();
      this.exitMenu = new System.Windows.Forms.MenuItem();
      this.editMenu = new System.Windows.Forms.MenuItem();
      this.undoMenu = new System.Windows.Forms.MenuItem();
      this.redoMenu = new System.Windows.Forms.MenuItem();
      this.sep1EditMenu = new System.Windows.Forms.MenuItem();
      this.cutMenu = new System.Windows.Forms.MenuItem();
      this.copyMenu = new System.Windows.Forms.MenuItem();
      this.pasteMenu = new System.Windows.Forms.MenuItem();
      this.deleteMenu = new System.Windows.Forms.MenuItem();
      this.sep2EditMenu = new System.Windows.Forms.MenuItem();
      this.selectAllMenu = new System.Windows.Forms.MenuItem();
      this.sep3EditMenu = new System.Windows.Forms.MenuItem();
      this.findAndReplaceMenu = new System.Windows.Forms.MenuItem();
      this.findMenu = new System.Windows.Forms.MenuItem();
      this.replaceMenu = new System.Windows.Forms.MenuItem();
      this.goToMenu = new System.Windows.Forms.MenuItem();
      this.sep4EditMenu = new System.Windows.Forms.MenuItem();
      this.compileMenu = new System.Windows.Forms.MenuItem();
      this.compileOutOfDateMenu = new System.Windows.Forms.MenuItem();
      this.compileAllMenu = new System.Windows.Forms.MenuItem();
      this.helpMenu = new System.Windows.Forms.MenuItem();
      this.helpContentsMenuItem = new System.Windows.Forms.MenuItem();
      this.aboutMenu = new System.Windows.Forms.MenuItem();
      this.listsPanel = new System.Windows.Forms.Panel();
      this.targetList = new System.Windows.Forms.ListBox();
      this.sourceTargetSplitter = new System.Windows.Forms.Splitter();
      this.sourceList = new System.Windows.Forms.ListBox();
      this.projectToolbar = new System.Windows.Forms.ToolBar();
      this.newProject = new System.Windows.Forms.ToolBarButton();
      this.openProject = new System.Windows.Forms.ToolBarButton();
      this.saveProject = new System.Windows.Forms.ToolBarButton();
      this.saveAsProject = new System.Windows.Forms.ToolBarButton();
      this.removeSource = new System.Windows.Forms.ToolBarButton();
      this.images = new System.Windows.Forms.ImageList(this.components);
      this.fileToolbar = new System.Windows.Forms.ToolBar();
      this.newFile = new System.Windows.Forms.ToolBarButton();
      this.openFile = new System.Windows.Forms.ToolBarButton();
      this.saveFile = new System.Windows.Forms.ToolBarButton();
      this.saveAsFile = new System.Windows.Forms.ToolBarButton();
      this.mainSplitter = new System.Windows.Forms.Splitter();
      this.displaysPanel = new System.Windows.Forms.Panel();
      this.addinSplitter = new System.Windows.Forms.Splitter();
      this.mainTab = new System.Windows.Forms.TabControl();
      this.editPage = new System.Windows.Forms.TabPage();
      this.editTab = new System.Windows.Forms.TabControl();
      this.editPopupMenu = new System.Windows.Forms.ContextMenu();
      this.saveFileEPM = new System.Windows.Forms.MenuItem();
      this.saveAsFileEPM = new System.Windows.Forms.MenuItem();
      this.closeFileEPM = new System.Windows.Forms.MenuItem();
      this.sep1EPM = new System.Windows.Forms.MenuItem();
      this.undoEPM = new System.Windows.Forms.MenuItem();
      this.redoEPM = new System.Windows.Forms.MenuItem();
      this.sep2EPM = new System.Windows.Forms.MenuItem();
      this.cutEPM = new System.Windows.Forms.MenuItem();
      this.copyEPM = new System.Windows.Forms.MenuItem();
      this.pasteEPM = new System.Windows.Forms.MenuItem();
      this.sep3EPM = new System.Windows.Forms.MenuItem();
      this.findEPM = new System.Windows.Forms.MenuItem();
      this.replaceEPM = new System.Windows.Forms.MenuItem();
      this.goToEPM = new System.Windows.Forms.MenuItem();
      this.sep4EPM = new System.Windows.Forms.MenuItem();
      this.printEPM = new System.Windows.Forms.MenuItem();
      this.printSetupEPM = new System.Windows.Forms.MenuItem();
      this.editToolbar = new System.Windows.Forms.ToolBar();
      this.compileEdit = new System.Windows.Forms.ToolBarButton();
      this.makeEdit = new System.Windows.Forms.ToolBarButton();
      this.buildEdit = new System.Windows.Forms.ToolBarButton();
      this.sep1Edit = new System.Windows.Forms.ToolBarButton();
      this.closeFileEdit = new System.Windows.Forms.ToolBarButton();
      this.sep2Edit = new System.Windows.Forms.ToolBarButton();
      this.findEdit = new System.Windows.Forms.ToolBarButton();
      this.replaceEdit = new System.Windows.Forms.ToolBarButton();
      this.sep3Edit = new System.Windows.Forms.ToolBarButton();
      this.cutEdit = new System.Windows.Forms.ToolBarButton();
      this.copyEdit = new System.Windows.Forms.ToolBarButton();
      this.pasteEdit = new System.Windows.Forms.ToolBarButton();
      this.sep4Edit = new System.Windows.Forms.ToolBarButton();
      this.undoEdit = new System.Windows.Forms.ToolBarButton();
      this.redoEdit = new System.Windows.Forms.ToolBarButton();
      this.sep5Edit = new System.Windows.Forms.ToolBarButton();
      this.printEdit = new System.Windows.Forms.ToolBarButton();
      this.printSetupEdit = new System.Windows.Forms.ToolBarButton();
      this.viewPage = new System.Windows.Forms.TabPage();
      this.viewTab = new System.Windows.Forms.TabControl();
      this.viewToolbar = new System.Windows.Forms.ToolBar();
      this.compileView = new System.Windows.Forms.ToolBarButton();
      this.sep1View = new System.Windows.Forms.ToolBarButton();
      this.closePageView = new System.Windows.Forms.ToolBarButton();
      this.sep2View = new System.Windows.Forms.ToolBarButton();
      this.findView = new System.Windows.Forms.ToolBarButton();
      this.sep3View = new System.Windows.Forms.ToolBarButton();
      this.copyView = new System.Windows.Forms.ToolBarButton();
      this.sep4View = new System.Windows.Forms.ToolBarButton();
      this.printView = new System.Windows.Forms.ToolBarButton();
      this.printSetupView = new System.Windows.Forms.ToolBarButton();
      this.optionTab = new System.Windows.Forms.TabPage();
      this.optionsList = new System.Windows.Forms.Panel();
      this.logLevelGroupBox = new System.Windows.Forms.GroupBox();
      this.label10 = new System.Windows.Forms.Label();
      this.label11 = new System.Windows.Forms.Label();
      this.label12 = new System.Windows.Forms.Label();
      this.label13 = new System.Windows.Forms.Label();
      this.label14 = new System.Windows.Forms.Label();
      this.logLevelTrackBar = new System.Windows.Forms.TrackBar();
      this.configDirectory = new System.Windows.Forms.TextBox();
      this.label9 = new System.Windows.Forms.Label();
      this.output = new System.Windows.Forms.TextBox();
      this.label8 = new System.Windows.Forms.Label();
      this.password = new System.Windows.Forms.TextBox();
      this.label7 = new System.Windows.Forms.Label();
      this.userId = new System.Windows.Forms.TextBox();
      this.label6 = new System.Windows.Forms.Label();
      this.server = new System.Windows.Forms.TextBox();
      this.label5 = new System.Windows.Forms.Label();
      this.package = new System.Windows.Forms.TextBox();
      this.label4 = new System.Windows.Forms.Label();
      this.database = new System.Windows.Forms.TextBox();
      this.label3 = new System.Windows.Forms.Label();
      this.targetDirectoryButton = new System.Windows.Forms.Button();
      this.sourceDirectoryButton = new System.Windows.Forms.Button();
      this.initialTargetDirectory = new System.Windows.Forms.TextBox();
      this.label2 = new System.Windows.Forms.Label();
      this.initialSourceDirectory = new System.Windows.Forms.TextBox();
      this.label1 = new System.Windows.Forms.Label();
      this.optionToolBar = new System.Windows.Forms.ToolBar();
      this.generatorsPage = new System.Windows.Forms.TabPage();
      this.generatorsList = new System.Windows.Forms.Panel();
      this.generatorToolBar = new System.Windows.Forms.ToolBar();
      this.logPage = new System.Windows.Forms.TabPage();
      this.logPanel = new System.Windows.Forms.Panel();
      this.logToolBar = new System.Windows.Forms.ToolBar();
      this.clearLog = new System.Windows.Forms.ToolBarButton();
      this.sep1Log = new System.Windows.Forms.ToolBarButton();
      this.printLog = new System.Windows.Forms.ToolBarButton();
      this.printSetupLog = new System.Windows.Forms.ToolBarButton();
      this.addinPanel = new System.Windows.Forms.Panel();
      this.toolTips = new System.Windows.Forms.ToolTip(this.components);
      this.viewerPopupMenu = new System.Windows.Forms.ContextMenu();
      this.copyVPM = new System.Windows.Forms.MenuItem();
      this.menuItem3 = new System.Windows.Forms.MenuItem();
      this.findVPM = new System.Windows.Forms.MenuItem();
      this.goToVPM = new System.Windows.Forms.MenuItem();
      this.menuItem7 = new System.Windows.Forms.MenuItem();
      this.PrintVPM = new System.Windows.Forms.MenuItem();
      this.printSetupVPM = new System.Windows.Forms.MenuItem();
      this.logPopupMenu = new System.Windows.Forms.ContextMenu();
      this.clearLPM = new System.Windows.Forms.MenuItem();
      this.menuItem2 = new System.Windows.Forms.MenuItem();
      this.copyLPM = new System.Windows.Forms.MenuItem();
      this.menuItem4 = new System.Windows.Forms.MenuItem();
      this.printLPM = new System.Windows.Forms.MenuItem();
      this.printSetupLPM = new System.Windows.Forms.MenuItem();
      this.printDialog = new System.Windows.Forms.PrintDialog();
      this.printDocument = new System.Drawing.Printing.PrintDocument();
      this.helpProvider = new System.Windows.Forms.HelpProvider();
      ((System.ComponentModel.ISupportInitialize)(this.editPositionStatus)).BeginInit();
      ((System.ComponentModel.ISupportInitialize)(this.editModeStatus)).BeginInit();
      ((System.ComponentModel.ISupportInitialize)(this.editMessageStatus)).BeginInit();
      ((System.ComponentModel.ISupportInitialize)(this.lastLogMessage)).BeginInit();
      this.listsPanel.SuspendLayout();
      this.displaysPanel.SuspendLayout();
      this.mainTab.SuspendLayout();
      this.editPage.SuspendLayout();
      this.viewPage.SuspendLayout();
      this.optionTab.SuspendLayout();
      this.optionsList.SuspendLayout();
      this.logLevelGroupBox.SuspendLayout();
      ((System.ComponentModel.ISupportInitialize)(this.logLevelTrackBar)).BeginInit();
      this.generatorsPage.SuspendLayout();
      this.logPage.SuspendLayout();
      this.SuspendLayout();
      // 
      // statusBar
      // 
      this.statusBar.Location = new System.Drawing.Point(0, 331);
      this.statusBar.Name = "statusBar";
      this.statusBar.Panels.AddRange(new System.Windows.Forms.StatusBarPanel[] {
                                                                                 this.editPositionStatus,
                                                                                 this.editModeStatus,
                                                                                 this.editMessageStatus,
                                                                                 this.lastLogMessage});
      this.statusBar.ShowPanels = true;
      this.statusBar.Size = new System.Drawing.Size(664, 22);
      this.statusBar.TabIndex = 0;
      // 
      // editPositionStatus
      // 
      this.editPositionStatus.AutoSize = System.Windows.Forms.StatusBarPanelAutoSize.Contents;
      this.editPositionStatus.Width = 10;
      // 
      // editModeStatus
      // 
      this.editModeStatus.AutoSize = System.Windows.Forms.StatusBarPanelAutoSize.Contents;
      this.editModeStatus.Width = 10;
      // 
      // editMessageStatus
      // 
      this.editMessageStatus.AutoSize = System.Windows.Forms.StatusBarPanelAutoSize.Contents;
      this.editMessageStatus.Width = 10;
      // 
      // lastLogMessage
      // 
      this.lastLogMessage.AutoSize = System.Windows.Forms.StatusBarPanelAutoSize.Spring;
      this.lastLogMessage.Width = 618;
      // 
      // ideMenu
      // 
      this.ideMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                                                            this.fileMenu,
                                                                            this.editMenu,
                                                                            this.helpMenu});
      // 
      // fileMenu
      // 
      this.fileMenu.Index = 0;
      this.fileMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                                                             this.newFileMenu,
                                                                             this.openFileMenu,
                                                                             this.saveFileMenu,
                                                                             this.saveAsFileMenu,
                                                                             this.removeFileMenu,
                                                                             this.sep0FileMenu,
                                                                             this.closeFileMenu,
                                                                             this.closeViewMenu,
                                                                             this.sep1FileMenu,
                                                                             this.openProjectMenu,
                                                                             this.newProjectMenu,
                                                                             this.saveProjectMenu,
                                                                             this.saveAsProjectMenu,
                                                                             this.sep2FileMenu,
                                                                             this.recentProjectsMenu,
                                                                             this.sep3FileMenu,
                                                                             this.printSetupMenu,
                                                                             this.printMenu,
                                                                             this.sep4Filemenu,
                                                                             this.exitMenu});
      this.fileMenu.Text = "File";
      // 
      // newFileMenu
      // 
      this.newFileMenu.Index = 0;
      this.newFileMenu.Text = "New File";
      this.newFileMenu.Click += new System.EventHandler(this.NewFileClick);
      // 
      // openFileMenu
      // 
      this.openFileMenu.Index = 1;
      this.openFileMenu.Text = "Open File";
      this.openFileMenu.Click += new System.EventHandler(this.OpenFileClick);
      // 
      // saveFileMenu
      // 
      this.saveFileMenu.Index = 2;
      this.saveFileMenu.Text = "Save File";
      this.saveFileMenu.Click += new System.EventHandler(this.SaveFileClick);
      // 
      // saveAsFileMenu
      // 
      this.saveAsFileMenu.Index = 3;
      this.saveAsFileMenu.Text = "Save As File";
      this.saveAsFileMenu.Click += new System.EventHandler(this.SaveAsFileClick);
      // 
      // removeFileMenu
      // 
      this.removeFileMenu.Index = 4;
      this.removeFileMenu.Text = "Remove File";
      this.removeFileMenu.Click += new System.EventHandler(this.RemoveFileClick);
      // 
      // sep0FileMenu
      // 
      this.sep0FileMenu.Index = 5;
      this.sep0FileMenu.Text = "-";
      // 
      // closeFileMenu
      // 
      this.closeFileMenu.Index = 6;
      this.closeFileMenu.Text = "Close Edit";
      this.closeFileMenu.Click += new System.EventHandler(this.CloseEditClick);
      // 
      // closeViewMenu
      // 
      this.closeViewMenu.Index = 7;
      this.closeViewMenu.Text = "Close View";
      this.closeViewMenu.Click += new System.EventHandler(this.CloseViewClick);
      // 
      // sep1FileMenu
      // 
      this.sep1FileMenu.Index = 8;
      this.sep1FileMenu.Text = "-";
      // 
      // openProjectMenu
      // 
      this.openProjectMenu.Index = 9;
      this.openProjectMenu.Text = "Open Project";
      this.openProjectMenu.Click += new System.EventHandler(this.OpenProjectClick);
      // 
      // newProjectMenu
      // 
      this.newProjectMenu.Index = 10;
      this.newProjectMenu.Text = "New Project";
      this.newProjectMenu.Click += new System.EventHandler(this.NewProjectClick);
      // 
      // saveProjectMenu
      // 
      this.saveProjectMenu.Index = 11;
      this.saveProjectMenu.Text = "Save Project";
      this.saveProjectMenu.Click += new System.EventHandler(this.SaveProjectClick);
      // 
      // saveAsProjectMenu
      // 
      this.saveAsProjectMenu.Index = 12;
      this.saveAsProjectMenu.Text = "Save As Project";
      this.saveAsProjectMenu.Click += new System.EventHandler(this.SaveAsProjectClick);
      // 
      // sep2FileMenu
      // 
      this.sep2FileMenu.Index = 13;
      this.sep2FileMenu.Text = "-";
      // 
      // recentProjectsMenu
      // 
      this.recentProjectsMenu.Index = 14;
      this.recentProjectsMenu.Text = "Recent Projects";
      // 
      // sep3FileMenu
      // 
      this.sep3FileMenu.Index = 15;
      this.sep3FileMenu.Text = "-";
      // 
      // printSetupMenu
      // 
      this.printSetupMenu.Index = 16;
      this.printSetupMenu.Text = "Print setup";
      this.printSetupMenu.Click += new System.EventHandler(this.PrintSetupClick);
      // 
      // printMenu
      // 
      this.printMenu.Index = 17;
      this.printMenu.Text = "Print";
      this.printMenu.Click += new System.EventHandler(this.PrintClick);
      // 
      // sep4Filemenu
      // 
      this.sep4Filemenu.Index = 18;
      this.sep4Filemenu.Text = "-";
      // 
      // exitMenu
      // 
      this.exitMenu.Index = 19;
      this.exitMenu.Text = "Exit";
      this.exitMenu.Click += new System.EventHandler(this.ExitClick);
      // 
      // editMenu
      // 
      this.editMenu.Index = 1;
      this.editMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                                                             this.undoMenu,
                                                                             this.redoMenu,
                                                                             this.sep1EditMenu,
                                                                             this.cutMenu,
                                                                             this.copyMenu,
                                                                             this.pasteMenu,
                                                                             this.deleteMenu,
                                                                             this.sep2EditMenu,
                                                                             this.selectAllMenu,
                                                                             this.sep3EditMenu,
                                                                             this.findAndReplaceMenu,
                                                                             this.goToMenu,
                                                                             this.sep4EditMenu,
                                                                             this.compileMenu,
                                                                             this.compileOutOfDateMenu,
                                                                             this.compileAllMenu});
      this.editMenu.Text = "Edit";
      // 
      // undoMenu
      // 
      this.undoMenu.Index = 0;
      this.undoMenu.Shortcut = System.Windows.Forms.Shortcut.CtrlZ;
      this.undoMenu.Text = "Undo";
      this.undoMenu.Click += new System.EventHandler(this.UndoClick);
      // 
      // redoMenu
      // 
      this.redoMenu.Index = 1;
      this.redoMenu.Shortcut = System.Windows.Forms.Shortcut.CtrlY;
      this.redoMenu.Text = "Redo";
      this.redoMenu.Click += new System.EventHandler(this.RedoClick);
      // 
      // sep1EditMenu
      // 
      this.sep1EditMenu.Index = 2;
      this.sep1EditMenu.Text = "-";
      // 
      // cutMenu
      // 
      this.cutMenu.Index = 3;
      this.cutMenu.Text = "Cut";
      this.cutMenu.Click += new System.EventHandler(this.CutClick);
      // 
      // copyMenu
      // 
      this.copyMenu.Index = 4;
      this.copyMenu.Text = "Copy";
      this.copyMenu.Click += new System.EventHandler(this.CopyClick);
      // 
      // pasteMenu
      // 
      this.pasteMenu.Index = 5;
      this.pasteMenu.Text = "Paste";
      this.pasteMenu.Click += new System.EventHandler(this.PasteClick);
      // 
      // deleteMenu
      // 
      this.deleteMenu.Index = 6;
      this.deleteMenu.Text = "Delete";
      this.deleteMenu.Click += new System.EventHandler(this.DeleteClick);
      // 
      // sep2EditMenu
      // 
      this.sep2EditMenu.Index = 7;
      this.sep2EditMenu.Text = "-";
      // 
      // selectAllMenu
      // 
      this.selectAllMenu.Index = 8;
      this.selectAllMenu.Text = "Select All";
      this.selectAllMenu.Click += new System.EventHandler(this.SelectAllClick);
      // 
      // sep3EditMenu
      // 
      this.sep3EditMenu.Index = 9;
      this.sep3EditMenu.Text = "-";
      // 
      // findAndReplaceMenu
      // 
      this.findAndReplaceMenu.Index = 10;
      this.findAndReplaceMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                                                                       this.findMenu,
                                                                                       this.replaceMenu});
      this.findAndReplaceMenu.Text = "Find and Replace";
      // 
      // findMenu
      // 
      this.findMenu.Index = 0;
      this.findMenu.Text = "Find";
      this.findMenu.Click += new System.EventHandler(this.FindClick);
      // 
      // replaceMenu
      // 
      this.replaceMenu.Index = 1;
      this.replaceMenu.Text = "Replace";
      this.replaceMenu.Click += new System.EventHandler(this.ReplaceClick);
      // 
      // goToMenu
      // 
      this.goToMenu.Index = 11;
      this.goToMenu.Text = "Go to";
      this.goToMenu.Click += new System.EventHandler(this.GoToClick);
      // 
      // sep4EditMenu
      // 
      this.sep4EditMenu.Index = 12;
      this.sep4EditMenu.Text = "-";
      // 
      // compileMenu
      // 
      this.compileMenu.Index = 13;
      this.compileMenu.Text = "Compile";
      this.compileMenu.Click += new System.EventHandler(this.CompileClick);
      // 
      // compileOutOfDateMenu
      // 
      this.compileOutOfDateMenu.Index = 14;
      this.compileOutOfDateMenu.Text = "Compile out of date";
      this.compileOutOfDateMenu.Click += new System.EventHandler(this.CompileOutOfDateClick);
      // 
      // compileAllMenu
      // 
      this.compileAllMenu.Index = 15;
      this.compileAllMenu.Text = "Compile All";
      this.compileAllMenu.Click += new System.EventHandler(this.CompileAllClick);
      // 
      // helpMenu
      // 
      this.helpMenu.Index = 2;
      this.helpMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                                                             this.helpContentsMenuItem,
                                                                             this.aboutMenu});
      this.helpMenu.Text = "Help";
      // 
      // helpContentsMenuItem
      // 
      this.helpContentsMenuItem.Index = 0;
      this.helpContentsMenuItem.Text = "Contents";
      this.helpContentsMenuItem.Click += new System.EventHandler(this.HelpContentsClick);
      // 
      // aboutMenu
      // 
      this.aboutMenu.Index = 1;
      this.aboutMenu.Text = "About";
      this.aboutMenu.Click += new System.EventHandler(this.AboutClick);
      // 
      // listsPanel
      // 
      this.listsPanel.Controls.Add(this.targetList);
      this.listsPanel.Controls.Add(this.sourceTargetSplitter);
      this.listsPanel.Controls.Add(this.sourceList);
      this.listsPanel.Controls.Add(this.projectToolbar);
      this.listsPanel.Controls.Add(this.fileToolbar);
      this.listsPanel.Dock = System.Windows.Forms.DockStyle.Left;
      this.listsPanel.Location = new System.Drawing.Point(0, 0);
      this.listsPanel.Name = "listsPanel";
      this.listsPanel.Size = new System.Drawing.Size(160, 331);
      this.listsPanel.TabIndex = 1;
      this.listsPanel.SizeChanged += new System.EventHandler(this.ListsPanelSizeChanged);
      // 
      // targetList
      // 
      this.targetList.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
      this.targetList.Dock = System.Windows.Forms.DockStyle.Fill;
      this.targetList.IntegralHeight = false;
      this.targetList.Location = new System.Drawing.Point(0, 204);
      this.targetList.Name = "targetList";
      this.targetList.Size = new System.Drawing.Size(160, 127);
      this.targetList.TabIndex = 1;
      this.targetList.DoubleClick += new System.EventHandler(this.TargetListOpenView);
      this.targetList.SelectedIndexChanged += new System.EventHandler(this.TargetListIndexChanged);
      // 
      // sourceTargetSplitter
      // 
      this.sourceTargetSplitter.Dock = System.Windows.Forms.DockStyle.Top;
      this.sourceTargetSplitter.Location = new System.Drawing.Point(0, 200);
      this.sourceTargetSplitter.Name = "sourceTargetSplitter";
      this.sourceTargetSplitter.Size = new System.Drawing.Size(160, 4);
      this.sourceTargetSplitter.TabIndex = 1;
      this.sourceTargetSplitter.TabStop = false;
      // 
      // sourceList
      // 
      this.sourceList.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
      this.sourceList.Dock = System.Windows.Forms.DockStyle.Top;
      this.sourceList.IntegralHeight = false;
      this.sourceList.Location = new System.Drawing.Point(0, 68);
      this.sourceList.Name = "sourceList";
      this.sourceList.Size = new System.Drawing.Size(160, 132);
      this.sourceList.Sorted = true;
      this.sourceList.TabIndex = 0;
      this.sourceList.SizeChanged += new System.EventHandler(this.SourceListSizeChanged);
      this.sourceList.DoubleClick += new System.EventHandler(this.SourceEdit);
      this.sourceList.DrawItem += new System.Windows.Forms.DrawItemEventHandler(this.SourceListDrawItem);
      this.sourceList.SelectedIndexChanged += new System.EventHandler(this.SourceListSelectedIndexChanged);
      // 
      // projectToolbar
      // 
      this.projectToolbar.Appearance = System.Windows.Forms.ToolBarAppearance.Flat;
      this.projectToolbar.Buttons.AddRange(new System.Windows.Forms.ToolBarButton[] {
                                                                                      this.newProject,
                                                                                      this.openProject,
                                                                                      this.saveProject,
                                                                                      this.saveAsProject,
                                                                                      this.removeSource});
      this.projectToolbar.Divider = false;
      this.projectToolbar.DropDownArrows = true;
      this.projectToolbar.ImageList = this.images;
      this.projectToolbar.Location = new System.Drawing.Point(0, 34);
      this.projectToolbar.Name = "projectToolbar";
      this.projectToolbar.ShowToolTips = true;
      this.projectToolbar.Size = new System.Drawing.Size(160, 34);
      this.projectToolbar.TabIndex = 1;
      this.projectToolbar.ButtonClick += new System.Windows.Forms.ToolBarButtonClickEventHandler(this.ProjectToolbarButtonClick);
      // 
      // newProject
      // 
      this.newProject.ImageIndex = 1;
      this.newProject.Tag = "NewProject";
      this.newProject.ToolTipText = "Create a new project";
      // 
      // openProject
      // 
      this.openProject.ImageIndex = 3;
      this.openProject.Tag = "OpenProject";
      this.openProject.ToolTipText = "Open an existing project";
      // 
      // saveProject
      // 
      this.saveProject.ImageIndex = 5;
      this.saveProject.Tag = "saveProject";
      this.saveProject.ToolTipText = "Save project";
      // 
      // saveAsProject
      // 
      this.saveAsProject.ImageIndex = 7;
      this.saveAsProject.Tag = "saveAsProject";
      this.saveAsProject.ToolTipText = "Save as a new project";
      // 
      // removeSource
      // 
      this.removeSource.ImageIndex = 9;
      this.removeSource.Tag = "RemoveSource";
      this.removeSource.ToolTipText = "Remove source from project";
      // 
      // images
      // 
      this.images.ImageSize = new System.Drawing.Size(24, 24);
      this.images.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("images.ImageStream")));
      this.images.TransparentColor = System.Drawing.Color.Silver;
      // 
      // fileToolbar
      // 
      this.fileToolbar.Appearance = System.Windows.Forms.ToolBarAppearance.Flat;
      this.fileToolbar.Buttons.AddRange(new System.Windows.Forms.ToolBarButton[] {
                                                                                   this.newFile,
                                                                                   this.openFile,
                                                                                   this.saveFile,
                                                                                   this.saveAsFile});
      this.fileToolbar.Divider = false;
      this.fileToolbar.DropDownArrows = true;
      this.helpProvider.SetHelpKeyword(this.fileToolbar, "Tables");
      this.helpProvider.SetHelpString(this.fileToolbar, "HelpString for Tables");
      this.fileToolbar.ImageList = this.images;
      this.fileToolbar.Location = new System.Drawing.Point(0, 0);
      this.fileToolbar.Name = "fileToolbar";
      this.helpProvider.SetShowHelp(this.fileToolbar, true);
      this.fileToolbar.ShowToolTips = true;
      this.fileToolbar.Size = new System.Drawing.Size(160, 34);
      this.fileToolbar.TabIndex = 0;
      this.fileToolbar.ButtonClick += new System.Windows.Forms.ToolBarButtonClickEventHandler(this.FileToolbarButtonClick);
      // 
      // newFile
      // 
      this.newFile.ImageIndex = 0;
      this.newFile.Tag = "NewFile";
      this.newFile.ToolTipText = "Create a new file";
      // 
      // openFile
      // 
      this.openFile.ImageIndex = 2;
      this.openFile.Tag = "OpenFile";
      this.openFile.ToolTipText = "Open an existing file";
      // 
      // saveFile
      // 
      this.saveFile.ImageIndex = 4;
      this.saveFile.Tag = "saveFile";
      this.saveFile.ToolTipText = "Save File";
      // 
      // saveAsFile
      // 
      this.saveAsFile.ImageIndex = 6;
      this.saveAsFile.Tag = "saveAsFile";
      this.saveAsFile.ToolTipText = "Save File as New Name";
      // 
      // mainSplitter
      // 
      this.mainSplitter.Location = new System.Drawing.Point(160, 0);
      this.mainSplitter.Name = "mainSplitter";
      this.mainSplitter.Size = new System.Drawing.Size(3, 331);
      this.mainSplitter.TabIndex = 2;
      this.mainSplitter.TabStop = false;
      // 
      // displaysPanel
      // 
      this.displaysPanel.Controls.Add(this.addinSplitter);
      this.displaysPanel.Controls.Add(this.mainTab);
      this.displaysPanel.Controls.Add(this.addinPanel);
      this.displaysPanel.Dock = System.Windows.Forms.DockStyle.Fill;
      this.displaysPanel.Location = new System.Drawing.Point(163, 0);
      this.displaysPanel.Name = "displaysPanel";
      this.displaysPanel.Size = new System.Drawing.Size(501, 331);
      this.displaysPanel.TabIndex = 3;
      // 
      // addinSplitter
      // 
      this.addinSplitter.Dock = System.Windows.Forms.DockStyle.Bottom;
      this.addinSplitter.Location = new System.Drawing.Point(0, 288);
      this.addinSplitter.Name = "addinSplitter";
      this.addinSplitter.Size = new System.Drawing.Size(501, 3);
      this.addinSplitter.TabIndex = 1;
      this.addinSplitter.TabStop = false;
      this.addinSplitter.Visible = false;
      // 
      // mainTab
      // 
      this.mainTab.Controls.Add(this.editPage);
      this.mainTab.Controls.Add(this.viewPage);
      this.mainTab.Controls.Add(this.optionTab);
      this.mainTab.Controls.Add(this.generatorsPage);
      this.mainTab.Controls.Add(this.logPage);
      this.mainTab.Dock = System.Windows.Forms.DockStyle.Fill;
      this.mainTab.Location = new System.Drawing.Point(0, 0);
      this.mainTab.Name = "mainTab";
      this.mainTab.SelectedIndex = 0;
      this.mainTab.Size = new System.Drawing.Size(501, 291);
      this.mainTab.TabIndex = 0;
      // 
      // editPage
      // 
      this.editPage.Controls.Add(this.editTab);
      this.editPage.Controls.Add(this.editToolbar);
      this.editPage.Location = new System.Drawing.Point(4, 22);
      this.editPage.Name = "editPage";
      this.editPage.Size = new System.Drawing.Size(493, 265);
      this.editPage.TabIndex = 0;
      this.editPage.Text = "Edit";
      // 
      // editTab
      // 
      this.editTab.ContextMenu = this.editPopupMenu;
      this.editTab.Dock = System.Windows.Forms.DockStyle.Fill;
      this.editTab.Location = new System.Drawing.Point(0, 34);
      this.editTab.Name = "editTab";
      this.editTab.SelectedIndex = 0;
      this.editTab.Size = new System.Drawing.Size(493, 231);
      this.editTab.TabIndex = 1;
      this.editTab.TabIndexChanged += new System.EventHandler(this.EditTabChanged);
      this.editTab.SelectedIndexChanged += new System.EventHandler(this.EditTabChanged);
      // 
      // editPopupMenu
      // 
      this.editPopupMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                                                                  this.saveFileEPM,
                                                                                  this.saveAsFileEPM,
                                                                                  this.closeFileEPM,
                                                                                  this.sep1EPM,
                                                                                  this.undoEPM,
                                                                                  this.redoEPM,
                                                                                  this.sep2EPM,
                                                                                  this.cutEPM,
                                                                                  this.copyEPM,
                                                                                  this.pasteEPM,
                                                                                  this.sep3EPM,
                                                                                  this.findEPM,
                                                                                  this.replaceEPM,
                                                                                  this.goToEPM,
                                                                                  this.sep4EPM,
                                                                                  this.printEPM,
                                                                                  this.printSetupEPM});
      // 
      // saveFileEPM
      // 
      this.saveFileEPM.Index = 0;
      this.saveFileEPM.Text = "Save File";
      this.saveFileEPM.Click += new System.EventHandler(this.SaveFileClick);
      // 
      // saveAsFileEPM
      // 
      this.saveAsFileEPM.Index = 1;
      this.saveAsFileEPM.Text = "Save As File";
      this.saveAsFileEPM.Click += new System.EventHandler(this.SaveAsFileClick);
      // 
      // closeFileEPM
      // 
      this.closeFileEPM.Index = 2;
      this.closeFileEPM.Text = "Close File";
      this.closeFileEPM.Click += new System.EventHandler(this.CloseEditClick);
      // 
      // sep1EPM
      // 
      this.sep1EPM.Index = 3;
      this.sep1EPM.Text = "-";
      // 
      // undoEPM
      // 
      this.undoEPM.Index = 4;
      this.undoEPM.Text = "Undo";
      this.undoEPM.Click += new System.EventHandler(this.UndoClick);
      // 
      // redoEPM
      // 
      this.redoEPM.Index = 5;
      this.redoEPM.Text = "Redo";
      this.redoEPM.Click += new System.EventHandler(this.RedoClick);
      // 
      // sep2EPM
      // 
      this.sep2EPM.Index = 6;
      this.sep2EPM.Text = "-";
      // 
      // cutEPM
      // 
      this.cutEPM.Index = 7;
      this.cutEPM.Text = "Cut";
      this.cutEPM.Click += new System.EventHandler(this.CutClick);
      // 
      // copyEPM
      // 
      this.copyEPM.Index = 8;
      this.copyEPM.Text = "Copy";
      this.copyEPM.Click += new System.EventHandler(this.CopyClick);
      // 
      // pasteEPM
      // 
      this.pasteEPM.Index = 9;
      this.pasteEPM.Text = "Paste";
      this.pasteEPM.Click += new System.EventHandler(this.PasteClick);
      // 
      // sep3EPM
      // 
      this.sep3EPM.Index = 10;
      this.sep3EPM.Text = "-";
      // 
      // findEPM
      // 
      this.findEPM.Index = 11;
      this.findEPM.Text = "Find";
      this.findEPM.Click += new System.EventHandler(this.FindClick);
      // 
      // replaceEPM
      // 
      this.replaceEPM.Index = 12;
      this.replaceEPM.Text = "Replace";
      this.replaceEPM.Click += new System.EventHandler(this.ReplaceClick);
      // 
      // goToEPM
      // 
      this.goToEPM.Index = 13;
      this.goToEPM.Text = "Go To";
      this.goToEPM.Click += new System.EventHandler(this.GoToClick);
      // 
      // sep4EPM
      // 
      this.sep4EPM.Index = 14;
      this.sep4EPM.Text = "-";
      // 
      // printEPM
      // 
      this.printEPM.Index = 15;
      this.printEPM.Text = "Print";
      this.printEPM.Click += new System.EventHandler(this.PrintClick);
      // 
      // printSetupEPM
      // 
      this.printSetupEPM.Index = 16;
      this.printSetupEPM.Text = "Print Setup";
      this.printSetupEPM.Click += new System.EventHandler(this.PrintSetupClick);
      // 
      // editToolbar
      // 
      this.editToolbar.Appearance = System.Windows.Forms.ToolBarAppearance.Flat;
      this.editToolbar.Buttons.AddRange(new System.Windows.Forms.ToolBarButton[] {
                                                                                   this.compileEdit,
                                                                                   this.makeEdit,
                                                                                   this.buildEdit,
                                                                                   this.sep1Edit,
                                                                                   this.closeFileEdit,
                                                                                   this.sep2Edit,
                                                                                   this.findEdit,
                                                                                   this.replaceEdit,
                                                                                   this.sep3Edit,
                                                                                   this.cutEdit,
                                                                                   this.copyEdit,
                                                                                   this.pasteEdit,
                                                                                   this.sep4Edit,
                                                                                   this.undoEdit,
                                                                                   this.redoEdit,
                                                                                   this.sep5Edit,
                                                                                   this.printEdit,
                                                                                   this.printSetupEdit});
      this.editToolbar.Divider = false;
      this.editToolbar.DropDownArrows = true;
      this.helpProvider.SetHelpKeyword(this.editToolbar, "BNF.html#Database");
      this.helpProvider.SetHelpNavigator(this.editToolbar, System.Windows.Forms.HelpNavigator.Topic);
      this.editToolbar.ImageList = this.images;
      this.editToolbar.Location = new System.Drawing.Point(0, 0);
      this.editToolbar.Name = "editToolbar";
      this.helpProvider.SetShowHelp(this.editToolbar, true);
      this.editToolbar.ShowToolTips = true;
      this.editToolbar.Size = new System.Drawing.Size(493, 34);
      this.editToolbar.TabIndex = 0;
      this.editToolbar.ButtonClick += new System.Windows.Forms.ToolBarButtonClickEventHandler(this.EditToolbarClick);
      // 
      // compileEdit
      // 
      this.compileEdit.ImageIndex = 10;
      this.compileEdit.Tag = "Compile";
      this.compileEdit.ToolTipText = "Compile Source";
      // 
      // makeEdit
      // 
      this.makeEdit.ImageIndex = 21;
      this.makeEdit.Tag = "Make";
      this.makeEdit.ToolTipText = "Make out of date source";
      // 
      // buildEdit
      // 
      this.buildEdit.ImageIndex = 20;
      this.buildEdit.Tag = "Build";
      this.buildEdit.ToolTipText = "Compile all source";
      // 
      // sep1Edit
      // 
      this.sep1Edit.Style = System.Windows.Forms.ToolBarButtonStyle.Separator;
      // 
      // closeFileEdit
      // 
      this.closeFileEdit.ImageIndex = 8;
      this.closeFileEdit.Tag = "CloseEdit";
      this.closeFileEdit.ToolTipText = "Close Edit Page";
      // 
      // sep2Edit
      // 
      this.sep2Edit.Style = System.Windows.Forms.ToolBarButtonStyle.Separator;
      // 
      // findEdit
      // 
      this.findEdit.ImageIndex = 18;
      this.findEdit.Tag = "Find";
      // 
      // replaceEdit
      // 
      this.replaceEdit.ImageIndex = 19;
      this.replaceEdit.Tag = "Replace";
      // 
      // sep3Edit
      // 
      this.sep3Edit.Style = System.Windows.Forms.ToolBarButtonStyle.Separator;
      // 
      // cutEdit
      // 
      this.cutEdit.ImageIndex = 11;
      this.cutEdit.Tag = "Cut";
      this.cutEdit.ToolTipText = "Cut Text";
      // 
      // copyEdit
      // 
      this.copyEdit.ImageIndex = 12;
      this.copyEdit.Tag = "Copy";
      this.copyEdit.ToolTipText = "Copy Text";
      // 
      // pasteEdit
      // 
      this.pasteEdit.ImageIndex = 13;
      this.pasteEdit.Tag = "Paste";
      this.pasteEdit.ToolTipText = "Paste Text";
      // 
      // sep4Edit
      // 
      this.sep4Edit.Style = System.Windows.Forms.ToolBarButtonStyle.Separator;
      // 
      // undoEdit
      // 
      this.undoEdit.ImageIndex = 16;
      this.undoEdit.Tag = "Undo";
      this.undoEdit.ToolTipText = "Undo Changes";
      // 
      // redoEdit
      // 
      this.redoEdit.ImageIndex = 17;
      this.redoEdit.Tag = "Redo";
      this.redoEdit.ToolTipText = "Redo Changes";
      // 
      // sep5Edit
      // 
      this.sep5Edit.Style = System.Windows.Forms.ToolBarButtonStyle.Separator;
      // 
      // printEdit
      // 
      this.printEdit.ImageIndex = 14;
      this.printEdit.Tag = "Print";
      this.printEdit.ToolTipText = "Print Source";
      // 
      // printSetupEdit
      // 
      this.printSetupEdit.ImageIndex = 15;
      this.printSetupEdit.Tag = "PrintSetup";
      this.printSetupEdit.ToolTipText = "Print Setup";
      // 
      // viewPage
      // 
      this.viewPage.Controls.Add(this.viewTab);
      this.viewPage.Controls.Add(this.viewToolbar);
      this.viewPage.Location = new System.Drawing.Point(4, 22);
      this.viewPage.Name = "viewPage";
      this.viewPage.Size = new System.Drawing.Size(493, 265);
      this.viewPage.TabIndex = 1;
      this.viewPage.Text = "View";
      // 
      // viewTab
      // 
      this.viewTab.Dock = System.Windows.Forms.DockStyle.Fill;
      this.viewTab.Location = new System.Drawing.Point(0, 40);
      this.viewTab.Name = "viewTab";
      this.viewTab.SelectedIndex = 0;
      this.viewTab.Size = new System.Drawing.Size(493, 225);
      this.viewTab.TabIndex = 2;
      // 
      // viewToolbar
      // 
      this.viewToolbar.Appearance = System.Windows.Forms.ToolBarAppearance.Flat;
      this.viewToolbar.Buttons.AddRange(new System.Windows.Forms.ToolBarButton[] {
                                                                                   this.compileView,
                                                                                   this.sep1View,
                                                                                   this.closePageView,
                                                                                   this.sep2View,
                                                                                   this.findView,
                                                                                   this.sep3View,
                                                                                   this.copyView,
                                                                                   this.sep4View,
                                                                                   this.printView,
                                                                                   this.printSetupView});
      this.viewToolbar.Divider = false;
      this.viewToolbar.DropDownArrows = true;
      this.viewToolbar.ImageList = this.images;
      this.viewToolbar.Location = new System.Drawing.Point(0, 0);
      this.viewToolbar.Name = "viewToolbar";
      this.viewToolbar.ShowToolTips = true;
      this.viewToolbar.Size = new System.Drawing.Size(493, 40);
      this.viewToolbar.TabIndex = 0;
      this.viewToolbar.ButtonClick += new System.Windows.Forms.ToolBarButtonClickEventHandler(this.ViewToolbarClick);
      // 
      // compileView
      // 
      this.compileView.Enabled = false;
      this.compileView.ImageIndex = 10;
      this.compileView.Tag = "Compile";
      this.compileView.ToolTipText = "Compile View ";
      this.compileView.Visible = false;
      // 
      // sep1View
      // 
      this.sep1View.Style = System.Windows.Forms.ToolBarButtonStyle.Separator;
      this.sep1View.Visible = false;
      // 
      // closePageView
      // 
      this.closePageView.ImageIndex = 8;
      this.closePageView.Tag = "CloseView";
      this.closePageView.ToolTipText = "Close View";
      // 
      // sep2View
      // 
      this.sep2View.Style = System.Windows.Forms.ToolBarButtonStyle.Separator;
      // 
      // findView
      // 
      this.findView.ImageIndex = 18;
      this.findView.Tag = "Find";
      // 
      // sep3View
      // 
      this.sep3View.Style = System.Windows.Forms.ToolBarButtonStyle.Separator;
      // 
      // copyView
      // 
      this.copyView.ImageIndex = 12;
      this.copyView.Tag = "Copy";
      this.copyView.ToolTipText = "Copy Text";
      // 
      // sep4View
      // 
      this.sep4View.Style = System.Windows.Forms.ToolBarButtonStyle.Separator;
      // 
      // printView
      // 
      this.printView.ImageIndex = 14;
      this.printView.Tag = "Print";
      this.printView.ToolTipText = "Print View ";
      // 
      // printSetupView
      // 
      this.printSetupView.ImageIndex = 15;
      this.printSetupView.Tag = "PrintSetup";
      this.printSetupView.ToolTipText = "Print Setup";
      // 
      // optionTab
      // 
      this.optionTab.Controls.Add(this.optionsList);
      this.optionTab.Controls.Add(this.optionToolBar);
      this.optionTab.Location = new System.Drawing.Point(4, 22);
      this.optionTab.Name = "optionTab";
      this.optionTab.Size = new System.Drawing.Size(493, 265);
      this.optionTab.TabIndex = 4;
      this.optionTab.Text = "Option";
      // 
      // optionsList
      // 
      this.optionsList.AutoScroll = true;
      this.optionsList.Controls.Add(this.logLevelGroupBox);
      this.optionsList.Controls.Add(this.configDirectory);
      this.optionsList.Controls.Add(this.label9);
      this.optionsList.Controls.Add(this.output);
      this.optionsList.Controls.Add(this.label8);
      this.optionsList.Controls.Add(this.password);
      this.optionsList.Controls.Add(this.label7);
      this.optionsList.Controls.Add(this.userId);
      this.optionsList.Controls.Add(this.label6);
      this.optionsList.Controls.Add(this.server);
      this.optionsList.Controls.Add(this.label5);
      this.optionsList.Controls.Add(this.package);
      this.optionsList.Controls.Add(this.label4);
      this.optionsList.Controls.Add(this.database);
      this.optionsList.Controls.Add(this.label3);
      this.optionsList.Controls.Add(this.targetDirectoryButton);
      this.optionsList.Controls.Add(this.sourceDirectoryButton);
      this.optionsList.Controls.Add(this.initialTargetDirectory);
      this.optionsList.Controls.Add(this.label2);
      this.optionsList.Controls.Add(this.initialSourceDirectory);
      this.optionsList.Controls.Add(this.label1);
      this.optionsList.Dock = System.Windows.Forms.DockStyle.Fill;
      this.optionsList.Location = new System.Drawing.Point(0, 26);
      this.optionsList.Name = "optionsList";
      this.optionsList.Size = new System.Drawing.Size(493, 239);
      this.optionsList.TabIndex = 4;
      // 
      // logLevelGroupBox
      // 
      this.logLevelGroupBox.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
      this.logLevelGroupBox.Controls.Add(this.label10);
      this.logLevelGroupBox.Controls.Add(this.label11);
      this.logLevelGroupBox.Controls.Add(this.label12);
      this.logLevelGroupBox.Controls.Add(this.label13);
      this.logLevelGroupBox.Controls.Add(this.label14);
      this.logLevelGroupBox.Controls.Add(this.logLevelTrackBar);
      this.logLevelGroupBox.Enabled = ((bool)(configurationAppSettings.GetValue("logLevelGroupBox.Enabled", typeof(bool))));
      this.logLevelGroupBox.Location = new System.Drawing.Point(296, 88);
      this.logLevelGroupBox.Name = "logLevelGroupBox";
      this.logLevelGroupBox.Size = new System.Drawing.Size(112, 160);
      this.logLevelGroupBox.TabIndex = 21;
      this.logLevelGroupBox.TabStop = false;
      this.logLevelGroupBox.Text = "Log Level";
      this.logLevelGroupBox.Visible = ((bool)(configurationAppSettings.GetValue("logLevelGroupBox.Visible", typeof(bool))));
      // 
      // label10
      // 
      this.label10.AutoSize = true;
      this.label10.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label10.Location = new System.Drawing.Point(56, 24);
      this.label10.Name = "label10";
      this.label10.Size = new System.Drawing.Size(46, 16);
      this.label10.TabIndex = 0;
      this.label10.Text = "Verbose";
      this.toolTips.SetToolTip(this.label10, "All messages shown (level 4)");
      // 
      // label11
      // 
      this.label11.AutoSize = true;
      this.label11.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label11.Location = new System.Drawing.Point(56, 50);
      this.label11.Name = "label11";
      this.label11.Size = new System.Drawing.Size(23, 16);
      this.label11.TabIndex = 1;
      this.label11.Text = "Info";
      this.toolTips.SetToolTip(this.label11, "Errors Warnings and Information message shown");
      // 
      // label12
      // 
      this.label12.AutoSize = true;
      this.label12.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label12.Location = new System.Drawing.Point(56, 76);
      this.label12.Name = "label12";
      this.label12.Size = new System.Drawing.Size(52, 16);
      this.label12.TabIndex = 2;
      this.label12.Text = "Warnings";
      this.toolTips.SetToolTip(this.label12, "Errors and warnings shown");
      // 
      // label13
      // 
      this.label13.AutoSize = true;
      this.label13.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label13.Location = new System.Drawing.Point(56, 102);
      this.label13.Name = "label13";
      this.label13.Size = new System.Drawing.Size(35, 16);
      this.label13.TabIndex = 3;
      this.label13.Text = "Errors";
      this.toolTips.SetToolTip(this.label13, "Only Error messages shown");
      // 
      // label14
      // 
      this.label14.AutoSize = true;
      this.label14.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label14.Location = new System.Drawing.Point(56, 128);
      this.label14.Name = "label14";
      this.label14.Size = new System.Drawing.Size(19, 16);
      this.label14.TabIndex = 4;
      this.label14.Text = "Off";
      this.toolTips.SetToolTip(this.label14, "No message shown (level 0)");
      // 
      // logLevelTrackBar
      // 
      this.logLevelTrackBar.LargeChange = 1;
      this.logLevelTrackBar.Location = new System.Drawing.Point(8, 16);
      this.logLevelTrackBar.Maximum = 4;
      this.logLevelTrackBar.Name = "logLevelTrackBar";
      this.logLevelTrackBar.Orientation = System.Windows.Forms.Orientation.Vertical;
      this.logLevelTrackBar.Size = new System.Drawing.Size(42, 136);
      this.logLevelTrackBar.TabIndex = 0;
      this.logLevelTrackBar.Value = ((int)(configurationAppSettings.GetValue("logLevelTrackBar.Value", typeof(int))));
      // 
      // configDirectory
      // 
      this.configDirectory.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right)));
      this.configDirectory.Location = new System.Drawing.Point(120, 16);
      this.configDirectory.Name = "configDirectory";
      this.configDirectory.ReadOnly = true;
      this.configDirectory.Size = new System.Drawing.Size(288, 20);
      this.configDirectory.TabIndex = 0;
      this.configDirectory.Text = "";
      // 
      // label9
      // 
      this.label9.AutoSize = true;
      this.label9.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label9.Location = new System.Drawing.Point(16, 18);
      this.label9.Name = "label9";
      this.label9.Size = new System.Drawing.Size(85, 16);
      this.label9.TabIndex = 18;
      this.label9.Text = "Config Directory";
      // 
      // output
      // 
      this.output.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right)));
      this.output.Location = new System.Drawing.Point(120, 112);
      this.output.Name = "output";
      this.output.Size = new System.Drawing.Size(168, 20);
      this.output.TabIndex = 6;
      this.output.Text = "";
      this.output.TextChanged += new System.EventHandler(this.OptionTextChange);
      // 
      // label8
      // 
      this.label8.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label8.Location = new System.Drawing.Point(16, 114);
      this.label8.Name = "label8";
      this.label8.Size = new System.Drawing.Size(100, 16);
      this.label8.TabIndex = 16;
      this.label8.Text = "Output";
      // 
      // password
      // 
      this.password.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right)));
      this.password.Location = new System.Drawing.Point(120, 208);
      this.password.Name = "password";
      this.password.Size = new System.Drawing.Size(168, 20);
      this.password.TabIndex = 10;
      this.password.Text = "";
      this.password.TextChanged += new System.EventHandler(this.OptionTextChange);
      // 
      // label7
      // 
      this.label7.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label7.Location = new System.Drawing.Point(16, 210);
      this.label7.Name = "label7";
      this.label7.Size = new System.Drawing.Size(100, 16);
      this.label7.TabIndex = 14;
      this.label7.Text = "Password";
      // 
      // userId
      // 
      this.userId.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right)));
      this.userId.Location = new System.Drawing.Point(120, 184);
      this.userId.Name = "userId";
      this.userId.Size = new System.Drawing.Size(168, 20);
      this.userId.TabIndex = 9;
      this.userId.Text = "";
      this.userId.TextChanged += new System.EventHandler(this.OptionTextChange);
      // 
      // label6
      // 
      this.label6.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label6.Location = new System.Drawing.Point(16, 186);
      this.label6.Name = "label6";
      this.label6.Size = new System.Drawing.Size(100, 16);
      this.label6.TabIndex = 12;
      this.label6.Text = "UserId";
      // 
      // server
      // 
      this.server.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right)));
      this.server.Location = new System.Drawing.Point(120, 160);
      this.server.Name = "server";
      this.server.Size = new System.Drawing.Size(168, 20);
      this.server.TabIndex = 8;
      this.server.Text = "";
      this.server.TextChanged += new System.EventHandler(this.OptionTextChange);
      // 
      // label5
      // 
      this.label5.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label5.Location = new System.Drawing.Point(16, 162);
      this.label5.Name = "label5";
      this.label5.Size = new System.Drawing.Size(100, 16);
      this.label5.TabIndex = 10;
      this.label5.Text = "Server";
      // 
      // package
      // 
      this.package.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right)));
      this.package.Location = new System.Drawing.Point(120, 136);
      this.package.Name = "package";
      this.package.Size = new System.Drawing.Size(168, 20);
      this.package.TabIndex = 7;
      this.package.Text = "";
      this.package.TextChanged += new System.EventHandler(this.OptionTextChange);
      // 
      // label4
      // 
      this.label4.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label4.Location = new System.Drawing.Point(16, 138);
      this.label4.Name = "label4";
      this.label4.Size = new System.Drawing.Size(100, 16);
      this.label4.TabIndex = 8;
      this.label4.Text = "Package";
      // 
      // database
      // 
      this.database.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right)));
      this.database.Location = new System.Drawing.Point(120, 88);
      this.database.Name = "database";
      this.database.Size = new System.Drawing.Size(168, 20);
      this.database.TabIndex = 5;
      this.database.Text = "";
      this.database.TextChanged += new System.EventHandler(this.OptionTextChange);
      // 
      // label3
      // 
      this.label3.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label3.Location = new System.Drawing.Point(16, 90);
      this.label3.Name = "label3";
      this.label3.Size = new System.Drawing.Size(100, 16);
      this.label3.TabIndex = 6;
      this.label3.Text = "Database";
      // 
      // targetDirectoryButton
      // 
      this.targetDirectoryButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
      this.targetDirectoryButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.targetDirectoryButton.Location = new System.Drawing.Point(408, 65);
      this.targetDirectoryButton.Name = "targetDirectoryButton";
      this.targetDirectoryButton.Size = new System.Drawing.Size(22, 18);
      this.targetDirectoryButton.TabIndex = 4;
      this.targetDirectoryButton.Tag = "InitialTargetDirectory";
      this.targetDirectoryButton.Text = "...";
      this.targetDirectoryButton.Click += new System.EventHandler(this.GeneratorDirectoryLookup);
      // 
      // sourceDirectoryButton
      // 
      this.sourceDirectoryButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
      this.sourceDirectoryButton.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.sourceDirectoryButton.Location = new System.Drawing.Point(408, 41);
      this.sourceDirectoryButton.Name = "sourceDirectoryButton";
      this.sourceDirectoryButton.Size = new System.Drawing.Size(22, 18);
      this.sourceDirectoryButton.TabIndex = 2;
      this.sourceDirectoryButton.Tag = "InitialSourceDirectory";
      this.sourceDirectoryButton.Text = "...";
      this.sourceDirectoryButton.Click += new System.EventHandler(this.GeneratorDirectoryLookup);
      // 
      // initialTargetDirectory
      // 
      this.initialTargetDirectory.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right)));
      this.initialTargetDirectory.Location = new System.Drawing.Point(120, 64);
      this.initialTargetDirectory.Name = "initialTargetDirectory";
      this.initialTargetDirectory.Size = new System.Drawing.Size(288, 20);
      this.initialTargetDirectory.TabIndex = 3;
      this.initialTargetDirectory.Text = "";
      this.initialTargetDirectory.TextChanged += new System.EventHandler(this.GeneratorDirectoryChange);
      // 
      // label2
      // 
      this.label2.AutoSize = true;
      this.label2.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label2.Location = new System.Drawing.Point(16, 66);
      this.label2.Name = "label2";
      this.label2.Size = new System.Drawing.Size(85, 16);
      this.label2.TabIndex = 2;
      this.label2.Text = "Target Directory";
      // 
      // initialSourceDirectory
      // 
      this.initialSourceDirectory.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right)));
      this.initialSourceDirectory.Location = new System.Drawing.Point(120, 40);
      this.initialSourceDirectory.Name = "initialSourceDirectory";
      this.initialSourceDirectory.Size = new System.Drawing.Size(288, 20);
      this.initialSourceDirectory.TabIndex = 1;
      this.initialSourceDirectory.Text = "";
      this.initialSourceDirectory.TextChanged += new System.EventHandler(this.GeneratorDirectoryChange);
      // 
      // label1
      // 
      this.label1.AutoSize = true;
      this.label1.FlatStyle = System.Windows.Forms.FlatStyle.System;
      this.label1.Location = new System.Drawing.Point(16, 42);
      this.label1.Name = "label1";
      this.label1.Size = new System.Drawing.Size(88, 16);
      this.label1.TabIndex = 0;
      this.label1.Text = "Source Directory";
      // 
      // optionToolBar
      // 
      this.optionToolBar.Appearance = System.Windows.Forms.ToolBarAppearance.Flat;
      this.optionToolBar.Divider = false;
      this.optionToolBar.DropDownArrows = true;
      this.optionToolBar.ImageList = this.images;
      this.optionToolBar.Location = new System.Drawing.Point(0, 0);
      this.optionToolBar.Name = "optionToolBar";
      this.optionToolBar.ShowToolTips = true;
      this.optionToolBar.Size = new System.Drawing.Size(493, 26);
      this.optionToolBar.TabIndex = 3;
      this.optionToolBar.TextAlign = System.Windows.Forms.ToolBarTextAlign.Right;
      // 
      // generatorsPage
      // 
      this.generatorsPage.Controls.Add(this.generatorsList);
      this.generatorsPage.Controls.Add(this.generatorToolBar);
      this.generatorsPage.Location = new System.Drawing.Point(4, 22);
      this.generatorsPage.Name = "generatorsPage";
      this.generatorsPage.Size = new System.Drawing.Size(493, 265);
      this.generatorsPage.TabIndex = 3;
      this.generatorsPage.Text = "Generators";
      // 
      // generatorsList
      // 
      this.generatorsList.AutoScroll = true;
      this.generatorsList.Dock = System.Windows.Forms.DockStyle.Fill;
      this.generatorsList.Location = new System.Drawing.Point(0, 26);
      this.generatorsList.Name = "generatorsList";
      this.generatorsList.Size = new System.Drawing.Size(493, 239);
      this.generatorsList.TabIndex = 3;
      // 
      // generatorToolBar
      // 
      this.generatorToolBar.Appearance = System.Windows.Forms.ToolBarAppearance.Flat;
      this.generatorToolBar.Divider = false;
      this.generatorToolBar.DropDownArrows = true;
      this.generatorToolBar.ImageList = this.images;
      this.generatorToolBar.Location = new System.Drawing.Point(0, 0);
      this.generatorToolBar.Name = "generatorToolBar";
      this.generatorToolBar.ShowToolTips = true;
      this.generatorToolBar.Size = new System.Drawing.Size(493, 26);
      this.generatorToolBar.TabIndex = 2;
      this.generatorToolBar.TextAlign = System.Windows.Forms.ToolBarTextAlign.Right;
      // 
      // logPage
      // 
      this.logPage.Controls.Add(this.logPanel);
      this.logPage.Controls.Add(this.logToolBar);
      this.logPage.Location = new System.Drawing.Point(4, 22);
      this.logPage.Name = "logPage";
      this.logPage.Size = new System.Drawing.Size(493, 265);
      this.logPage.TabIndex = 2;
      this.logPage.Text = "Log";
      // 
      // logPanel
      // 
      this.logPanel.Dock = System.Windows.Forms.DockStyle.Fill;
      this.logPanel.Location = new System.Drawing.Point(0, 40);
      this.logPanel.Name = "logPanel";
      this.logPanel.Size = new System.Drawing.Size(493, 225);
      this.logPanel.TabIndex = 2;
      // 
      // logToolBar
      // 
      this.logToolBar.Appearance = System.Windows.Forms.ToolBarAppearance.Flat;
      this.logToolBar.Buttons.AddRange(new System.Windows.Forms.ToolBarButton[] {
                                                                                  this.clearLog,
                                                                                  this.sep1Log,
                                                                                  this.printLog,
                                                                                  this.printSetupLog});
      this.logToolBar.Divider = false;
      this.logToolBar.DropDownArrows = true;
      this.logToolBar.ImageList = this.images;
      this.logToolBar.Location = new System.Drawing.Point(0, 0);
      this.logToolBar.Name = "logToolBar";
      this.logToolBar.ShowToolTips = true;
      this.logToolBar.Size = new System.Drawing.Size(493, 40);
      this.logToolBar.TabIndex = 1;
      this.logToolBar.ButtonClick += new System.Windows.Forms.ToolBarButtonClickEventHandler(this.LogToolbarClick);
      // 
      // clearLog
      // 
      this.clearLog.ImageIndex = 16;
      this.clearLog.Tag = "Clear";
      this.clearLog.ToolTipText = "Clear Log";
      // 
      // sep1Log
      // 
      this.sep1Log.Style = System.Windows.Forms.ToolBarButtonStyle.Separator;
      // 
      // printLog
      // 
      this.printLog.ImageIndex = 14;
      this.printLog.Tag = "Print";
      this.printLog.ToolTipText = "Print Log";
      // 
      // printSetupLog
      // 
      this.printSetupLog.ImageIndex = 15;
      this.printSetupLog.Tag = "PrintSetup";
      this.printSetupLog.ToolTipText = "Print Setup";
      // 
      // addinPanel
      // 
      this.addinPanel.Dock = System.Windows.Forms.DockStyle.Bottom;
      this.addinPanel.Location = new System.Drawing.Point(0, 291);
      this.addinPanel.Name = "addinPanel";
      this.addinPanel.Size = new System.Drawing.Size(501, 40);
      this.addinPanel.TabIndex = 2;
      this.addinPanel.Visible = false;
      // 
      // viewerPopupMenu
      // 
      this.viewerPopupMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                                                                    this.copyVPM,
                                                                                    this.menuItem3,
                                                                                    this.findVPM,
                                                                                    this.goToVPM,
                                                                                    this.menuItem7,
                                                                                    this.PrintVPM,
                                                                                    this.printSetupVPM});
      // 
      // copyVPM
      // 
      this.copyVPM.Index = 0;
      this.copyVPM.Text = "Copy";
      this.copyVPM.Click += new System.EventHandler(this.CopyClick);
      // 
      // menuItem3
      // 
      this.menuItem3.Index = 1;
      this.menuItem3.Text = "-";
      // 
      // findVPM
      // 
      this.findVPM.Index = 2;
      this.findVPM.Text = "Find";
      this.findVPM.Click += new System.EventHandler(this.FindClick);
      // 
      // goToVPM
      // 
      this.goToVPM.Index = 3;
      this.goToVPM.Text = "Go To";
      this.goToVPM.Click += new System.EventHandler(this.GoToClick);
      // 
      // menuItem7
      // 
      this.menuItem7.Index = 4;
      this.menuItem7.Text = "-";
      // 
      // PrintVPM
      // 
      this.PrintVPM.Index = 5;
      this.PrintVPM.Text = "Print";
      this.PrintVPM.Click += new System.EventHandler(this.PrintClick);
      // 
      // printSetupVPM
      // 
      this.printSetupVPM.Index = 6;
      this.printSetupVPM.Text = "Print Setup";
      this.printSetupVPM.Click += new System.EventHandler(this.PrintSetupClick);
      // 
      // logPopupMenu
      // 
      this.logPopupMenu.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                                                                 this.clearLPM,
                                                                                 this.menuItem2,
                                                                                 this.copyLPM,
                                                                                 this.menuItem4,
                                                                                 this.printLPM,
                                                                                 this.printSetupLPM});
      // 
      // clearLPM
      // 
      this.clearLPM.Index = 0;
      this.clearLPM.Text = "Clear";
      this.clearLPM.Click += new System.EventHandler(this.ClearClick);
      // 
      // menuItem2
      // 
      this.menuItem2.Index = 1;
      this.menuItem2.Text = "-";
      // 
      // copyLPM
      // 
      this.copyLPM.Index = 2;
      this.copyLPM.Text = "Copy";
      // 
      // menuItem4
      // 
      this.menuItem4.Index = 3;
      this.menuItem4.Text = "-";
      // 
      // printLPM
      // 
      this.printLPM.Index = 4;
      this.printLPM.Text = "Print";
      // 
      // printSetupLPM
      // 
      this.printSetupLPM.Index = 5;
      this.printSetupLPM.Text = "Print Setup";
      // 
      // printDialog
      // 
      this.printDialog.Document = this.printDocument;
      // 
      // printDocument
      // 
      this.printDocument.PrintPage += new System.Drawing.Printing.PrintPageEventHandler(this.PrintDocumentPrintPage);
      // 
      // helpProvider
      // 
      this.helpProvider.HelpNamespace = ((string)(configurationAppSettings.GetValue("helpProvider.HelpNamespace", typeof(string))));
      // 
      // IDEForm
      // 
      this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
      this.ClientSize = new System.Drawing.Size(664, 353);
      this.Controls.Add(this.displaysPanel);
      this.Controls.Add(this.mainSplitter);
      this.Controls.Add(this.listsPanel);
      this.Controls.Add(this.statusBar);
      this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
      this.Menu = this.ideMenu;
      this.Name = "IDEForm";
      this.Text = "AnyDBIDE (incorporating JPortal)";
      this.Closing += new System.ComponentModel.CancelEventHandler(this.IDEFormClosing);
      this.Load += new System.EventHandler(this.IDEFormLoad);
      ((System.ComponentModel.ISupportInitialize)(this.editPositionStatus)).EndInit();
      ((System.ComponentModel.ISupportInitialize)(this.editModeStatus)).EndInit();
      ((System.ComponentModel.ISupportInitialize)(this.editMessageStatus)).EndInit();
      ((System.ComponentModel.ISupportInitialize)(this.lastLogMessage)).EndInit();
      this.listsPanel.ResumeLayout(false);
      this.displaysPanel.ResumeLayout(false);
      this.mainTab.ResumeLayout(false);
      this.editPage.ResumeLayout(false);
      this.viewPage.ResumeLayout(false);
      this.optionTab.ResumeLayout(false);
      this.optionsList.ResumeLayout(false);
      this.logLevelGroupBox.ResumeLayout(false);
      ((System.ComponentModel.ISupportInitialize)(this.logLevelTrackBar)).EndInit();
      this.generatorsPage.ResumeLayout(false);
      this.logPage.ResumeLayout(false);
      this.ResumeLayout(false);

    }
    #endregion
    /// <summary>
    /// The main entry point for the application.
    /// </summary>
    [STAThread]
    static void Main()
    {
      //Application.EnableVisualStyles();
      Application.Run(new IDEForm());
    }
    Project project = new Project();
    internal SortedList domains = new SortedList();
    private void Log(string value, TraceLevel level)
    {
      TextArea area = logText.ActiveTextAreaControl.TextArea;
      area.InsertString(value + "\r\n");
      Trace.WriteLine(value);
    }
    public string LogVerbose
    {
      set
      {
        if (logLevelTrackBar.Value >= 4)
          Log("Debug: " + value, TraceLevel.Verbose);
      }
    }
    public string LogInfo
    {
      set
      {
        if (logLevelTrackBar.Value >= 3)
          Log("Info: " + value, TraceLevel.Info);
      }
    }
    public string LogWarning
    {
      set
      {
        if (logLevelTrackBar.Value >= 2)
          Log("Warning: " + value, TraceLevel.Warning);
      }
    }
    public string LogError                           
    {
      set
      {
        if (logLevelTrackBar.Value >= 1)
          Log("Error: " + value, TraceLevel.Error);
      }
    }
    public string MessageStatus 
    {
      set { editMessageStatus.Text = value; }
    }
    public string PositionStatus 
    {
      set { editPositionStatus.Text = value; }
    }
    public string ModeStatus 
    {
      set { editModeStatus.Text = value; }
    }
    private Config config;
    private TextEditorControl lastEditor;
    private TextEditorControl lastViewer;
    public TextEditorControl LastEditor {get{return lastEditor;}set{lastEditor=value;}}
    public TextEditorControl LastViewer {get{return lastViewer;}set{lastViewer=value;}}
    private void SaveConfig()
    {
      config["ScreenX"] = this.Location.X.ToString();
      config["ScreenY"] = this.Location.Y.ToString();
      config["ScreenWidth"] = this.Size.Width.ToString();
      config["ScreenHeight"] = this.Size.Height.ToString();
      config["ListsPanelWidth"] = listsPanel.Width.ToString();
      config["SourceListHeight"] = sourceList.Height.ToString();
      config.Save();
    }
    private int IntParse(string value)
    {
      try
      {
        return int.Parse(value);
      }
      catch
      {
        return 0;
      }
    }
    private void RecentProjectClick(object sender, System.EventArgs e)
    {
      MenuItem item = sender as MenuItem;
      if (item != null)
      {
        LogInfo = item.Text;
        OpenProjectFile(item.Text);
      }
    }
    private void AddToRecentProjectMenu(string name)
    {
      MenuItem item = new MenuItem();
      item.Text = config[name];
      item.Click += new EventHandler(RecentProjectClick);
      recentProjectsMenu.MenuItems.Add(item);
    }
    private void LoadRecentProjectsMenu()
    {
      int noRecentProjects = IntParse(config["NoRecentProjects"] as string);
      for (int i=0; i<noRecentProjects; i++)
      {
        string name = "Project"+i.ToString();
        AddToRecentProjectMenu(name);
      }
    }
    private void LoadConfig()
    {
      config = Config.Instance;
      config.FileName = configDirectory.Text;
      try
      {
        FileInfo fileInfo = new FileInfo(config.FileName);
        if (fileInfo.Exists == false)
        {
          config.FileName = Application.StartupPath+@"\Config\Initial.AnyDBIde.xml";
          fileInfo = new FileInfo(config.FileName);
        }
        if (fileInfo.Exists)
        {
          config.Load();
          this.Location = new Point(IntParse(config["ScreenX"]), IntParse(config["ScreenY"]));
          this.Size = new Size(IntParse(config["ScreenWidth"]), IntParse(config["ScreenHeight"]));
          int width = IntParse(config["ListsPanelWidth"]);
          int height = IntParse(config["SourceListHeight"]);
          if (width < ClientSize.Width)
            listsPanel.Width = width;
          if (height < ClientSize.Height)
            sourceList.Height = height;
          LoadRecentProjectsMenu();
        }
      }
      finally
      {
        config.FileName = configDirectory.Text;
      }
    }
    private TextEditorControl logText;
    private void SetupLogText()
    {
      logText = new TextEditorControl();
      logText.Document.TextContent = "";
      logText.AllowDrop = true;
      logText.Dock = DockStyle.Fill;
      logText.ShowEOLMarkers = false;
      logText.ShowInvalidLines = false;
      logText.ShowSpaces = false;
      logText.ShowTabs = false;
      logPanel.Controls.Add(logText);
      logText.Font = new System.Drawing.Font("Courier New", 10F);
      logText.Location = new System.Drawing.Point(0, 34);
      logText.Name = "logText";
      logText.Size = new System.Drawing.Size(469, 271);
      logText.TabIndex = 3;
    }
    private void IDEFormLoad(object sender, System.EventArgs e)
    {
      using (SplashForm splash = new SplashForm())
      {
        splash.Visible = false;
        splash.Show();
        splash.Refresh();
        splash.Visible = true;
        SetupLogText();
        configDirectory.Text = Application.StartupPath+@"\Config\AnyDBIde.xml";
        splash.Info = "Loading Configuration ...";
        LoadConfig();
        sourceDirectoryButton.Tag = initialSourceDirectory;
        targetDirectoryButton.Tag = initialTargetDirectory;
        splash.Info = "Loading Generators ...";
        LoadGenerators();
        string LastProject = config["LastProject"];
        if (LastProject.Length > 0
          && System.IO.File.Exists(LastProject))
        {
          splash.Info = "Opening last project :"+LastProject;
          OpenProjectFile(LastProject);
        }
      }
    }
    private void IDEFormClosing(object sender, System.ComponentModel.CancelEventArgs e)
    {
      ClearProject();
      SaveConfig();
    }
    private void GeneratorClick(object sender, System.EventArgs e)
    {
      CheckBox cb = sender as CheckBox;
      if (cb != null)
      {
        string name = ((CheckBox)sender).Name;
        LogVerbose ="" + name + " clicked.";
        if (project.Switch.IndexOfKey(name) == -1)
          project.Switch.Add(name, cb.Checked.ToString());
        else
          project.Switch[name] = cb.Checked.ToString();
      }
    }
    private void OptionTextChange(object sender, System.EventArgs e)
    {
      TextBox tb = sender as TextBox;
      if (tb != null)
      {
        string name = tb.Name;
        if (project.Switch.IndexOfKey(name) == -1)
          project.Switch.Add(name, tb.Text);
        else
          project.Switch[name] = tb.Text;
      }
    }
    private void GeneratorDirectoryChange(object sender, System.EventArgs e)
    {
      TextBox tb = sender as TextBox;
      if (tb != null)
      {
        string name = tb.Name;
        tb.BackColor = Color.White;
        char ch = ' ';
        if (tb.Text.Length > 0)
          ch = tb.Text[tb.Text.Length-1];
        if (Directory.Exists(tb.Text) && ch == '\\')
        {
          if (project.Switch.IndexOfKey(name) == -1)
            project.Switch.Add(name, tb.Text);
          else
            project.Switch[name] = tb.Text;
          LogVerbose ="" + tb.Text + " changed.";
        }
        else if (tb.Text.Trim().Length > 0)
          tb.BackColor = Color.LightPink;
      }
    }
    private void GeneratorDirectoryLookup(object sender, System.EventArgs e)
    {
      Button bb = sender as Button;
      if (bb == null)
        return;
      TextBox tb = bb.Tag as TextBox;
      if (tb == null)
        return;
      OpenFileDialog od = new OpenFileDialog();
      if (tb.Text.Length > 0)
        od.InitialDirectory = new FileInfo(tb.Text).DirectoryName;
      else
        od.InitialDirectory = initialTargetDirectory.Text;
      od.Filter = "All files (*.*)|*.*";
      od.FilterIndex = 0;
      od.CheckPathExists = true;
      od.CheckFileExists = false;
      od.RestoreDirectory = false;
      od.FileName = "not used";
      if(od.ShowDialog() == DialogResult.OK)
      {
        tb.Text = new FileInfo(od.FileName).DirectoryName;
        if (tb.Text[tb.Text.Length-1] != '\\')
          tb.Text = tb.Text + "\\";
      }
    }
    private class GeneratorMethods
    {
      public MethodInfo description;
      public MethodInfo documentation;
      public MethodInfo generate;
      public MethodInfo flags;
    }
    private class GeneratorFlag
    {
      public string name;
      public Flag flag;
      public GeneratorFlag(string name, Flag flag)
      {
        this.name = name;
        this.flag = flag;
      }
    }
    private void UpdateFlagModify(object sender, System.EventArgs e)
    {
      CheckBox cb = sender as CheckBox;
      if (cb != null)
      {
        GeneratorFlag g = cb.Tag as GeneratorFlag;
        if (g != null && g.flag != null)
        {
          g.flag.value = cb.Checked;
          if (project.Switch.IndexOfKey(g.name) == -1)
            project.Switch.Add(g.name, g.flag.value);
          else
            project.Switch[g.name] = g.flag.value;
        }
      }
    }
    private void UpdateFlags(GeneratorMethods generator)
    {
      object[] parameters = new object[0];
      using (FlagForm form = new FlagForm())
      {
        ToolTip toolTip = new ToolTip();
        form.Text = (string)generator.description.Invoke(null, parameters);
        java.util.Vector vector = (java.util.Vector)generator.flags.Invoke(null, parameters);
        for (int i=0; i<vector.size(); i++)
        {
          Flag flag = (Flag)vector.elementAt(i);
          string name = form.Text.Trim()+":"+flag.name; 
          CheckBox check = new CheckBox();
          if (project.Switch.IndexOfKey(name) == -1)
            check.Checked = flag.value;
          else
            check.Checked = bool.Parse((string)project.Switch[name]);
          check.Text = flag.description;
          check.Left = 8;
          check.Top = 8 + i*24;
          check.Width = form.Width - 24;
          check.Tag = new GeneratorFlag(name, flag);  
          check.Click += new EventHandler(UpdateFlagModify);
          //check.Appearance = Appearance.Button; 
          //check.Anchor |= AnchorStyles.Right;
          form.Controls.Add(check); 
          toolTip.SetToolTip(check, flag.name);
        }
        form.ShowDialog();
      }
    }
    private void GeneratorDocumentation(object sender, System.EventArgs e)
    {
      Button bb2 = sender as Button;
      if (bb2 != null)
      {
        GeneratorMethods generator = bb2.Tag as GeneratorMethods;
        object[] parameters = new object[0];
        if (generator.flags == null)
          MessageBox.Show((string)generator.documentation.Invoke(null, parameters), "Simple Documentation");
        else
          UpdateFlags(generator);
      }
    }
    private void LoadGenerators()
    {
      LogVerbose ="Loading Generators";
      int noAddins = int.Parse(config["NoAddins"]);
      SortedList list = new SortedList();
      int u = 0;
      for (int addin=0; addin<noAddins; addin++)
      {
        string assemblyName = config[File.AddinTag+addin.ToString()];
        if (assemblyName[0] == '.' && assemblyName[1] == '\\')
          assemblyName = Application.StartupPath+assemblyName.Substring(1);
        Assembly assembly = Assembly.LoadFile(assemblyName);
        System.Reflection.Module[] modules = assembly.GetLoadedModules(true);
        for (int m=0; m<modules.Length; m++)
        {
          System.Type[] types = modules[m].GetTypes();
          for (int t=0; t<types.Length; t++)
            if (list.Contains(types[t].Name) == false)
              list.Add(types[t].Name, types[t]);
        }
      }
      for (int t=0; t<list.Count; t++)
      {
        System.Type type = (System.Type) list.GetByIndex(t);
        object[] parameters = new object[0];
        MethodInfo description = type.GetMethod("description");
        if (description == null)
          continue;
        MethodInfo documentation = type.GetMethod("documentation");
        if (documentation == null)
          continue;
        MethodInfo flags = type.GetMethod("flags");
        System.Type[] args = {typeof(bbd.jportal.Database), typeof(String), typeof(java.io.PrintWriter)};
        MethodInfo generate = type.GetMethod("generate", args);
        if (generate == null)
          continue;
        GeneratorMethods generator = new GeneratorMethods();
        generator.description = description;
        generator.documentation = documentation;
        generator.generate = generate;
        generator.flags = flags;
        string toolTipText = (string)description.Invoke(null, parameters);
        if (flags != null)
        {
          java.util.Vector vector = (java.util.Vector)generator.flags.Invoke(null, parameters);
          for (int i=0; i<vector.size(); i++)
          {
            Flag flag = (Flag)vector.elementAt(i);
            string name = toolTipText.Trim()+":"+flag.name; 
            if (project.Switch.IndexOfKey(name) != -1)
              flag.value = bool.Parse((string)project.Switch[name]);
          }
        }
        CheckBox cb = new CheckBox();
        Button bb = new Button();
        Button bb2 = new Button();
        TextBox tb = new TextBox();
        toolTips.SetToolTip(cb, (string)toolTipText);
        cb.Top = u++ * 19; bb.Top = cb.Top+1; bb2.Top = cb.Top+1; tb.Top = cb.Top;
        cb.Left = 4; bb.Left = 168; bb2.Left = 192; tb.Left = 214;
        cb.Width = 160; bb.Width = 22; bb2.Width = 18;
        bb.Height = tb.Height-2; bb2.Height = bb.Height; tb.Height = 20; 
        bb.Text = "..."; 
        if (flags != null)
          bb2.Text = "!";
        else
          bb2.Text = "?";
        bb.Tag = tb;
        cb.Name = type.Name + "Generator";
        cb.Text = type.Name;
        cb.Tag = tb;
        tb.Tag = generate;
        tb.BackColor = Color.White;
        tb.Width = 240;
        tb.Name = cb.Text + "Directory";
        tb.Anchor = AnchorStyles.Top | AnchorStyles.Right | AnchorStyles.Left;
        object bb2TagText = documentation.Invoke(null, parameters);
        bb2.Tag = generator;
        cb.Click += new EventHandler(GeneratorClick);
        tb.TextChanged += new EventHandler(GeneratorDirectoryChange);
        bb.Click += new EventHandler(GeneratorDirectoryLookup);
        bb2.Click += new EventHandler(GeneratorDocumentation);
        generatorsList.Controls.Add(cb);
        generatorsList.Controls.Add(bb);
        generatorsList.Controls.Add(bb2);
        generatorsList.Controls.Add(tb);
      }
    }
    private void ListsPanelSizeChanged(object sender, System.EventArgs e)
    {
      if (listsPanel.Width < 160)
        listsPanel.Width = 160;
    }
    private void SourceListSizeChanged(object sender, System.EventArgs e)
    {
      if (sourceList.Height < 120)
        sourceList.Height = 120;
    }
    private int GetEditTabPage(string name)
    {
      for (int i=0; i<editTab.TabPages.Count; i++)
      {
        System.Windows.Forms.TabPage tp = editTab.TabPages[i];
        if (tp.Text.ToUpper().Equals(name.ToUpper()))
          return i;
      }
      return -1;
    }
    private void CheckSaveEdit(Source source)
    {
      string name = source.ToString("S");
      int no = GetEditTabPage(name);
      if (no != -1)
      {
        EditorSet editorSet = editTab.TabPages[no].Tag as EditorSet;
        editorSet.CheckSave();
      }
    }
    private void SyncCurrentDirectory(int index)
    {
      Source source = sourceList.Items[index] as Source;
      if (source != null)
      {
        FileInfo info = new FileInfo(source.FileName);
        LogVerbose ="Changing to " + info.DirectoryName;
        Directory.SetCurrentDirectory(info.DirectoryName);
      }
    }
    private void SyncEditTabToSourceList()
    {
      if (editTab.SelectedTab == null)
        return;
      EditorSet editorSet = editTab.SelectedTab.Tag as EditorSet;
      if (editorSet != null)
        lastEditor = editorSet.editor;
      string Lookup = editTab.SelectedTab.Text.ToUpper();
      for (int i=0; i<sourceList.Items.Count; i++)
      {
        if (sourceList.Items[i].ToString().ToUpper().Equals(Lookup))
        {
          sourceList.SelectedIndex = i;
          return;
        }
      }
    }
    private void EditTabChanged(object sender, System.EventArgs e)
    {
      SyncEditTabToSourceList();
    }
    private void AddEditTab(Source source)
    {
      string name = source.ToString("S");
      int no = GetEditTabPage(name);
      if (no == -1)
        new EditorSet(this, source, editPopupMenu, editTab, ref no);
      editTab.SelectedTab = editTab.TabPages[no];
    }
    internal void EditorEnter(object sender, System.EventArgs e)
    {
      lastEditor = sender as TextEditorControl;
    }
    private void SourceEdit(object sender, System.EventArgs e)
    {
      mainTab.SelectedTab = editPage;
      if (sourceList.SelectedIndex != -1)
        AddEditTab((Source)sourceList.SelectedItem);
    }
    private void SourceListDrawItem(object sender, System.Windows.Forms.DrawItemEventArgs e)
    {
      if (e.Index < 0)
      {
        LogVerbose = "e.Index < 0";
        return;
      }
      Source source = sourceList.Items[e.Index] as Source;
      if (source == null)
      {
        LogVerbose = "source == null";
        return;
      }
      Color color = (e.State & DrawItemState.Selected) == DrawItemState.Selected ? Color.White : Color.Black;
      Brush brush = new SolidBrush(color);
      try
      {
        int x = 0;
        int y = e.Index*sourceList.ItemHeight;
        Font font = e.Font;
        string s = source.ToString();
        e.DrawBackground();
        e.DrawFocusRectangle();
        e.Graphics.DrawString(s, font, brush, x, y);
        LogVerbose = String.Format("{0},{1},{2}",x,y,s);
      }
      finally
      {
        brush.Dispose();
      }
    }
    private void SourceListSelectedIndexChanged(object sender, System.EventArgs e)
    {
      if (sourceList.SelectedIndex != -1)
      {
        try 
        {
          SyncCurrentDirectory(sourceList.SelectedIndex);
        }
        catch (Exception)
        {
        }
        Source source = sourceList.SelectedItem as Source;
        if (source != null)
        {
          toolTips.SetToolTip(sourceList, source.ToString("L"));
          targetList.Items.Clear();
          for (int t=0; t<source.Count; t++)
            targetList.Items.Add(source[t]);
        }
      }
    }
    private void TargetListIndexChanged(object sender, System.EventArgs e)
    {
      if (targetList.SelectedIndex != -1)
      {
        Target target = targetList.SelectedItem as Target;
        if (target != null)
          toolTips.SetToolTip(targetList, target.ToString("L"));
      }
    }
    private int GetViewTabPage(Target target)
    {
      for (int i=0; i<viewTab.TabPages.Count; i++)
      {
        TabPage tp = viewTab.TabPages[i];
        if (tp.Tag.Equals(target))
          return i;
      }
      return -1;
    }
    private void AddViewTab(Target target)
    {
      int no = GetViewTabPage(target);
      if (no == -1)
        new ViewerSet(this, target, viewerPopupMenu, viewTab, ref no);
      viewTab.SelectedTab = viewTab.TabPages[no];
    }
    private void TargetListOpenView(object sender, System.EventArgs e)
    {
      if (targetList.SelectedIndex == -1)
        return;
      Target target = targetList.SelectedItem as Target;
      if (target == null)
        return;
      mainTab.SelectedTab = viewPage;
      AddViewTab(target);
    }
    private string GetFileName(bool exists)
    {
      OpenFileDialog od = new OpenFileDialog();
      od.Title = "Open source file";
      od.InitialDirectory = initialSourceDirectory.Text;
      od.Filter = "si files (*.si)|*.si|All files (*.*)|*.*";
      od.FilterIndex = 0;
      od.CheckFileExists = exists;
      od.RestoreDirectory = false;
      if(od.ShowDialog() == DialogResult.OK)
      {
        initialSourceDirectory.Text = new FileInfo(od.FileName).DirectoryName + "\\";
        return od.FileName;
      }
      return null;
    }
    private string GetProjectName(bool exists)
    {
      OpenFileDialog od = new OpenFileDialog();
      od.Title = "Open project file";
      od.InitialDirectory = initialSourceDirectory.Text;
      od.Filter = "Project files (*.xml)|*.xml|All files (*.*)|*.*";
      od.FilterIndex = 0;
      od.CheckFileExists = exists;
      od.RestoreDirectory = false;
      if(od.ShowDialog() == DialogResult.OK)
      {
        initialSourceDirectory.Text = new FileInfo(od.FileName).DirectoryName + "\\";
        return od.FileName;
      }
      return null;
    }
    private void AddSource(string fileName, bool exists)
    {
      int at = project.Add(fileName, exists);
      Source source = project[at];
      int no = sourceList.Items.IndexOf(source);
      if (no == -1)
        no = sourceList.Items.Add(source);
      sourceList.SelectedIndex = no;
    }
    private void RunGenerators(bbd.jportal.Database database, java.io.PrintWriter log)
    {
      object[] args = {database, typeof(string), log};
      for (int i=0; i<generatorsList.Controls.Count; i++)
      {
        CheckBox cb = generatorsList.Controls[i] as CheckBox;
        if (cb == null || cb.Checked == false)
          continue;
        TextBox tb = cb.Tag as TextBox;
        if (tb == null)
        {
          LogVerbose ="No directory information text, skipping generation for "+cb.Text;
          continue;
        }
        char ch = ' ';
        if (tb.Text.Length > 0)
          ch = tb.Text[tb.Text.Length-1];
        if (Directory.Exists(tb.Text) && ch == '\\')
          tb.BackColor = Color.White;
        else if (tb.Text.Trim().Length > 0)
        {
          LogVerbose ="Invalid directory information text, skipping generation for "+cb.Text;
          tb.BackColor = Color.LightPink;
          continue;
        }
        args[1] = tb.Text;
        MethodInfo method = tb.Tag as MethodInfo;
        if (method == null)
        {
          LogVerbose ="No proper generator for "+cb.Text;
          continue;
        }
        try
        {
          method.Invoke(null, args);
        }
        catch(Exception ex)
        {
          for (Exception x = ex; x != null; x = x.InnerException)
            LogError = x.Message;
          LogError ="Generation failed for "+cb.Text;
        }
      }
    }
    private void RunCompile(Source source, ref bool clearError)
    {
      if (source != null)
      {
        ShowAddin(ShowErrorAddin);
        SetButton(ShowErrorAddin.CloseButton, "Close");
        if (clearError == true)
        {
          clearError = false;
          ShowErrorAddin.LogRichTextBox.Clear();
          Refresh();
        }
        source.Clear();
        java.io.StringWriter writer = new java.io.StringWriter();
        try
        {
          java.io.PrintWriter log = new java.io.PrintWriter(writer);
          try
          {
            string currDir = Directory.GetCurrentDirectory();
            try
            {
              ShowErrorAddin.LogRichTextBox.AppendText(string.Format("Compiling: {0}\n",source.FileName));
              ShowErrorAddin.LogRichTextBox.Refresh();
              editMessageStatus.Text = string.Format("Compiling: {0}\n",source.FileName);
              statusBar.Refresh();
              FileInfo info = new FileInfo(source.FileName);
              LogVerbose ="Changing to " + info.DirectoryName;
              Directory.SetCurrentDirectory(info.DirectoryName);
              LogInfo ="Compiling " + info.Name;
              bbd.jportal.Database database = JPortal.run(info.FullName, log);
              source.EraseTargets();
              RunGenerators(database, log);
            }
            finally
            {
              Directory.SetCurrentDirectory(currDir);
            }
          }
          finally
          {
            log.flush();
            log.close();
          }
          string result = writer.ToString();
          char[] sep = {'\n'};
          string[] lines = result.Split(sep);
          string message = "";
          for (int i=0; i<lines.Length; i++)
          {
            string line = lines[i].Trim();
            if (line.Length == 0)
              continue;
            if (line.IndexOf("Code: ") == 0)
              source.Add(line.Substring(6).Trim());
            else if (line.IndexOf("DDL: ") == 0)
              source.Add(line.Substring(5).Trim());
            else if (line.IndexOf("(") == -1)
            {
              ShowErrorAddin.LogRichTextBox.AppendText(source.FileName+": "+line+"\n");
              message += line + "\n";
            }
            LogInfo = line;
          }
          if (message.Length > 0)
            MessageBox.Show(message, source.FileName);
        }
        finally
        {
          writer.close();
        }
      }
    }
    private void CloseEdit(TabPage tp)
    {
      EditorSet editorSet = tp.Tag as EditorSet;
      if (editorSet != null)
        editorSet.Close();
      tp.Controls.Clear();
    }
    private void ClearEdits()
    {
      for (int i=0; i<editTab.TabCount; i++)
        CloseEdit(editTab.TabPages[i]);
      editTab.Controls.Clear();
    }
    private void ClearViews()
    {
      for (int i=0; i<viewTab.TabCount; i++)
      {
        TabPage tp = viewTab.TabPages[i];
        tp.Controls.Clear();
      }
      viewTab.Controls.Clear();
    }
    private void ClearSwitches(Panel panel)
    {
      for (int i=0; i<panel.Controls.Count; i++)
      {
        Control control = panel.Controls[i] as Control;
        string name = control.Name;
        if (control is CheckBox)
          ((CheckBox)control).Checked = false;
        else if (control is TextBox
        && ((TextBox)control).ReadOnly == false)
          ((TextBox)control).Text = "";
      }
    }
    private void ClearSwitches()
    {
      ClearSwitches(generatorsList);
      ClearSwitches(optionsList);
    }
    private void AddToRecentProjects()
    {
      string upperName = project.FileName.ToUpper();
      if (project.FileName.Length > 0
      && config[upperName].Length == 0)
      {
        int noRecentProjects = IntParse(config["NoRecentProjects"] as string);
        string name = "Project"+noRecentProjects.ToString();
        noRecentProjects++;
        LogVerbose ="Adding " + name + " " + project.FileName;
        config[name] = project.FileName;
        config[upperName] = name;
        config["NoRecentProjects"] = noRecentProjects.ToString();
        AddToRecentProjectMenu(name);
      }
    }
    private void ClearProject()
    {
      if (project.Modified)
      {
        DialogResult rc = MessageBox.Show(project.FileName+" has changed. Do you wish to save?", "Project has changed", MessageBoxButtons.YesNo);
        if (rc == DialogResult.Yes)
          project.Save();
      }
      AddToRecentProjects();
      ClearEdits();
      ClearViews();
      sourceList.Items.Clear();
      targetList.Items.Clear();
      ClearSwitches();
    }
    private void SetSwitches(Panel panel)
    {
      for (int i=0; i<panel.Controls.Count; i++)
      {
        Control control = panel.Controls[i] as Control;
        string name = control.Name;
        if (project.Switch.IndexOfKey(name) == -1)
          continue;
        if (control is CheckBox)
          ((CheckBox)control).Checked = bool.Parse((string)project.Switch[name]);
        else if (control is TextBox)
          ((TextBox)control).Text = (string)project.Switch[name];
      }
    }
    private void SetSwitches()
    {
      SetSwitches(generatorsList);
      SetSwitches(optionsList);
    }
    private void FileToolbarButtonClick(object sender, System.Windows.Forms.ToolBarButtonClickEventArgs e)
    {
      switch (e.Button.Tag.ToString())
      {
      case "NewFile":
        NewFileClick(sender, e);
        break;
      case "OpenFile":
        OpenFileClick(sender, e);
        break;
      case "saveFile":
        SaveFileClick(sender, e);
        break;
      case "saveAsFile":
        SaveAsFileClick(sender, e);
        break;
      }
    }
    private void ProjectToolbarButtonClick(object sender, System.Windows.Forms.ToolBarButtonClickEventArgs e)
    {
      switch (e.Button.Tag.ToString())
      {
      case "NewProject":
        NewProjectClick(sender, e);
        break;
      case "OpenProject":
        OpenProjectClick(sender, e);
        break;
      case "saveProject":
        SaveProjectClick(sender, e);
        break;
      case "saveAsProject":
        SaveAsProjectClick(sender, e);
        break;
      case "RemoveSource":
        RemoveFileClick(sender, e);
        break;
      }
    }
    private void EditToolbarClick(object sender, System.Windows.Forms.ToolBarButtonClickEventArgs e)
    {
      switch (e.Button.Tag.ToString())
      {
      case "Compile":
        CompileClick(sender, e);
        break;
      case "Make":
        CompileOutOfDateClick(sender, e);
        break;
      case "Build":
        CompileAllClick(sender, e);
        break;
      case "CloseEdit":
        CloseEditClick(sender, e);
        break;
      case "Find":
        FindClick(sender, e);
        break;
      case "Replace":
        ReplaceClick(sender, e);
        break;
      case "Cut":
        CutClick(sender, e);
        break;
      case "Copy":
        CopyClick(sender, e);
        break;
      case "Paste":
        PasteClick(sender, e);
        break;
      case "Delete":
        DeleteClick(sender, e);
        break;
      case "Undo":
        UndoClick(sender, e);
        break;
      case "Redo":
        RedoClick(sender, e);
        break;
      case "Print":
        PrintClick(sender, e);
        break;
      case "PrintSetup":
        PrintSetupClick(sender, e);
        break;
      }
    }
    private void ViewToolbarClick(object sender, System.Windows.Forms.ToolBarButtonClickEventArgs e)
    {
      switch (e.Button.Tag.ToString())
      {
        case "Compile":
          CompileViewClick(sender, e);
          break;
        case "CloseView":
          CloseViewClick(sender, e);
          break;
        case "Find":
          FindClick(sender, e);
          break;
        case "Copy":
          CopyClick(sender, e);
          break;
        case "Print":
          PrintClick(sender, e);
          break;
        case "PrintSetup":
          PrintSetupClick(sender, e);
          break;
      }
    }
    private void LogToolbarClick(object sender, System.Windows.Forms.ToolBarButtonClickEventArgs e)
    {
      switch (e.Button.Tag.ToString())
      {
        case "Clear":
          ClearClick(sender, e);
          break;
        case "Copy":
          CopyClick(sender, e);
          break;
        case "Print":
          PrintClick(sender, e);
          break;
        case "PrintSetup":
          PrintSetupClick(sender, e);
          break;
      }
    }
    private void ClearClick(object sender, System.EventArgs e)
    {
      logText.Document.TextContent = "";
    }
    private void OpenFileClick(object sender, System.EventArgs e)
    {
      try
      {
        string fileName = GetFileName(true);
        if (fileName == null)
          return;
        LogVerbose ="Open file: "+fileName;
        AddSource(fileName, true);
        SourceEdit(sender, e);
      }
      catch (Exception ex)
      {
        LogError = ex.Message;
        MessageBox.Show(ex.Message);
      }
    }
    private void NewFileClick(object sender, System.EventArgs e)
    {
      try
      {
        string fileName = GetFileName(false);
        if (fileName == null)
          return;
        bool exists = new FileInfo(fileName).Exists;
        LogVerbose ="Create a new file"+(exists?" (already exists)":"")+":"+fileName;
        AddSource(fileName, false);
        SourceEdit(sender, e);
      }
      catch (Exception ex)
      {
        LogError = ex.Message;
        MessageBox.Show(ex.Message);
      }
    }
    private void SaveFileClick(object sender, System.EventArgs e)
    {
      LogVerbose = "Save file";
      if (editTab.SelectedTab != null)
      {
        TabPage tabPage = (TabPage) editTab.SelectedTab;
        EditorSet editorSet = (EditorSet) tabPage.Tag;
        if (editorSet != null)
          editorSet.Save();
      }
    }
    private void SaveAsFileClick(object sender, System.EventArgs e)
    {
      if (editTab.SelectedTab != null)
      {
        TabPage tabPage = (TabPage) editTab.SelectedTab;
        EditorSet editorSet = (EditorSet) tabPage.Tag;
        if (editorSet != null)
        {
          string fileName = GetFileName(false);
          if (fileName == null)
            return;
          if (editorSet.SaveAs(fileName) == false)
            return;
          CloseEdit(editTab.SelectedTab);
          editTab.Controls.Remove(editTab.SelectedTab);
          // Bizarre to have to remove and add to get redraw
          sourceList.Items.Remove(editorSet.source);
          sourceList.SelectedIndex = sourceList.Items.Add(editorSet.source);
          sourceList.Refresh();
        }
      }
    }
    private void CloseEditClick(object sender, System.EventArgs e)
    {
      if (editTab.SelectedTab != null)
      {
        CloseEdit(editTab.SelectedTab);
        editTab.Controls.Remove(editTab.SelectedTab);
      }
    }
    private void CloseViewClick(object sender, System.EventArgs e)
    {
      if (viewTab.SelectedTab != null)
      {
        ViewerSet viewerSet = viewTab.SelectedTab.Tag as ViewerSet;
        if (viewerSet != null)
          viewerSet.Close();
        viewTab.Controls.Remove(viewTab.SelectedTab);
      }
    }
    private void CloseAllViews(object sender, System.EventArgs e)
    {
      for (int i=viewTab.TabCount-1; i>=0; i--)
      {
        viewTab.TabIndex = i;
        CloseViewClick(sender, e);
      }
    }
    private void RemoveFileClick(object sender, System.EventArgs e)
    {
      LogVerbose ="Remove file";
      if (sourceList.SelectedItem != null)
      {
        int n = sourceList.SelectedIndex;
        project.Remove(sourceList.SelectedItem as Source);
        sourceList.Items.Remove(sourceList.SelectedItem);
        if (n < sourceList.Items.Count)
          sourceList.SelectedIndex = n;
        else
          sourceList.SelectedIndex = sourceList.Items.Count - 1;
      }
    }
    private void NewProjectClick(object sender, System.EventArgs e)
    {
      LogVerbose ="New Project";
      string fileName = GetProjectName(false);
      if (fileName == null)
        return;
      ClearProject();
      project = new Project(fileName);
      config["LastProject"] = fileName;
    }
    private void OpenProjectFile(string fileName)
    {
      ClearProject();
      project = new Project(fileName);
      config["LastProject"] = fileName;
      project.Load();
      LogVerbose ="Project Loaded";
      for (int i=0; i<project.Count; i++)
        AddSource(project[i].FileName, project[i].Exists);
      SetSwitches();
    }
    private void OpenProjectClick(object sender, System.EventArgs e)
    {
      LogVerbose ="Open Project";
      string fileName = GetProjectName(true);
      if (fileName == null)
        return;
      try
      {
        OpenProjectFile(fileName);
      }
      catch
      {
        LogError = "Project Load Failed";
        NewProjectClick(sender, e);
      }
    }
    private void SaveProjectClick(object sender, System.EventArgs e)
    {
      LogVerbose ="Save Project";
      project.Save();
    }
    private void SaveAsProjectClick(object sender, System.EventArgs e)
    {
      LogVerbose ="Save As Project";
      string fileName = GetProjectName(false);
      if (fileName == null)
        return;
      if (new FileInfo(fileName).Exists == true)
      {
        if (fileName.ToUpper().Equals(project.FileName.ToUpper()) == false)
        {
          DialogResult rc = MessageBox.Show("The project "+fileName+" already exists, do you wish to overwrite?", "Save Project As", MessageBoxButtons.YesNo);
          if (rc == DialogResult.No)
            return;
        }
      }
      project.FileName = fileName;
      SaveProjectClick(sender, e);
    }
    private void SetEditActiveControl()
    {
      switch (mainTab.SelectedTab.Text)
      {
        case "Edit":
          if (editTab.TabCount > 0)
            ActiveControl = lastEditor;
          break;
        case "View":
          if (viewTab.TabCount > 0)
            ActiveControl = lastViewer;
          break;
        case "Log":
          ActiveControl = logText;
          break;
      }
    }
    private void UndoClick(object sender, System.EventArgs e)
    {
      SetEditActiveControl();
      RichTextBox rtb = ActiveControl as RichTextBox;
      if (rtb != null)
      {
        rtb.Undo();
        return;
      }
      TextEditorControl editor = ActiveControl as TextEditorControl;
      if (editor != null)
      {
        editor.Undo();
        return;
      }
      TextBox tb = ActiveControl as TextBox;
      if (tb != null)
      {
        tb.Undo();
        return;
      }
    }
    private void RedoClick(object sender, System.EventArgs e)
    {
      SetEditActiveControl();
      RichTextBox rtb = ActiveControl as RichTextBox;
      if (rtb != null)
      {
        rtb.Redo();
        return;
      }
      TextEditorControl editor = ActiveControl as TextEditorControl;
      if (editor != null)
      {
        editor.Redo();
        return;
      }
    }
    private void CutClick(object sender, System.EventArgs e)
    {
      SetEditActiveControl();
      RichTextBox rtb = ActiveControl as RichTextBox;
      if (rtb != null)
      {
        rtb.Cut();
        return;
      }
      TextEditorControl editor = ActiveControl as TextEditorControl;
      if (editor != null)
      {
        editor.ActiveTextAreaControl.TextArea.ClipboardHandler.Cut(null, null);
        return;
      }
      TextBox tb = ActiveControl as TextBox;
      if (tb != null)
      {
        tb.Cut();
        return;
      }
    }
    private void CopyClick(object sender, System.EventArgs e)
    {
      SetEditActiveControl();
      RichTextBox rtb = ActiveControl as RichTextBox;
      if (rtb != null)
      {
        rtb.Copy();
        return;
      }
      TextEditorControl editor = ActiveControl as TextEditorControl;
      if (editor != null)
      {
        editor.ActiveTextAreaControl.TextArea.ClipboardHandler.Copy(null, null);
        return;
      }
      TextBox tb = ActiveControl as TextBox;
      if (tb != null)
      {
        tb.Copy();
        return;
      }
    }
    private void PasteClick(object sender, System.EventArgs e)
    {
      SetEditActiveControl();
      RichTextBox rtb = ActiveControl as RichTextBox;
      if (rtb != null)
      {
        rtb.Paste();
        return;
      }
      TextEditorControl editor = ActiveControl as TextEditorControl;
      if (editor != null)
      {
        editor.ActiveTextAreaControl.TextArea.ClipboardHandler.Paste(null, null);
        return;
      }
      TextBox tb = ActiveControl as TextBox;
      if (tb != null)
      {
        tb.Paste();
        return;
      }
    }
    private void DeleteClick(object sender, System.EventArgs e)
    {
      SetEditActiveControl();
      TextEditorControl editor = ActiveControl as TextEditorControl;
      if (editor != null)
      {
        editor.ActiveTextAreaControl.TextArea.ClipboardHandler.Delete(null, null);
        return;
      }
    }
    private void SelectAllClick(object sender, System.EventArgs e)
    {
      SetEditActiveControl();
      RichTextBox rtb = ActiveControl as RichTextBox;
      if (rtb != null)
      {
        rtb.SelectAll();
        return;
      }
      TextEditorControl editor = ActiveControl as TextEditorControl;
      if (editor != null)
      {
        editor.ActiveTextAreaControl.TextArea.ClipboardHandler.SelectAll(null, null);
        return;
      }
      TextBox tb = ActiveControl as TextBox;
      if (tb != null)
      {
        tb.SelectAll();
        return;
      }
    }
    private string AddinHint(string text)
    {
      return string.Format("Associated with {0}. Click to close.", text);
    }
    private void SetButton(Button button, string text)
    {
      button.Text = text;
      button.Click -= new EventHandler(AddinDoubleClick);
      button.Click += new EventHandler(AddinDoubleClick);
    }
    private void FindClick(object sender, System.EventArgs e)
    {
      SetEditActiveControl();
      TextEditorControl editor = ActiveControl as TextEditorControl;
      if (editor != null)
      {
        EditSetBase est = editor.Tag as EditSetBase;
        est.Closing += new EventHandler(AddinClosing);
        FindAddin.EditSet = est;
        toolTips.SetToolTip(FindAddin.CloseButton, AddinHint(est.tabPage.Text));
        SetButton(FindAddin.CloseButton, est.tabPage.Text);
        ShowAddin(FindAddin);
      }
    }
    private void ReplaceClick(object sender, System.EventArgs e)
    {
      SetEditActiveControl();
      TextEditorControl editor = ActiveControl as TextEditorControl;
      if (editor != null)
      {
        EditSetBase est = editor.Tag as EditSetBase;
        est.Closing += new EventHandler(AddinClosing);
        ReplaceAddin.EditSet = est;
        toolTips.SetToolTip(ReplaceAddin.CloseButton, AddinHint(est.tabPage.Text));
        SetButton(ReplaceAddin.CloseButton, est.tabPage.Text);
        ShowAddin(ReplaceAddin);
      }
    }
    private GoToLineUC goToAddin;
    public GoToLineUC GoToAddin
    {
      get 
      {
        if (goToAddin == null)
        {
          goToAddin = GoToLineUC.Instance;
          goToAddin.Dock = DockStyle.Top;
          goToAddin.DoubleClick += new EventHandler(AddinDoubleClick);
          addinPanel.Controls.Add(goToAddin);
        }
        return goToAddin; 
      }
    }
    private FindUC findAddin;
    public FindUC FindAddin
    {
      get 
      {
        if (findAddin == null)
        {
          findAddin = FindUC.Instance;
          findAddin.Dock = DockStyle.Top;
          findAddin.DoubleClick += new EventHandler(AddinDoubleClick);
          addinPanel.Controls.Add(findAddin);
        }
        return findAddin; 
      }
    }
    private ShowErrorUC showErrorAddin;
    public ShowErrorUC ShowErrorAddin
    {
      get 
      {
        if (showErrorAddin == null)
        {
          showErrorAddin = ShowErrorUC.Instance;
          showErrorAddin.Dock = DockStyle.Fill;
          showErrorAddin.DoubleClick += new EventHandler(AddinDoubleClick);
          addinPanel.Controls.Add(showErrorAddin);
        }
        return showErrorAddin; 
      }
    }
    private ReplaceUC replaceAddin;
    public ReplaceUC ReplaceAddin
    {
      get 
      {
        if (replaceAddin == null)
        {
          replaceAddin = ReplaceUC.Instance;
          replaceAddin.Dock = DockStyle.Top;
          replaceAddin.DoubleClick += new EventHandler(AddinDoubleClick);
          addinPanel.Controls.Add(replaceAddin);
        }
        return replaceAddin; 
      }
    }
    private void ShowAddin(Control control)
    {
      foreach (Control addin in addinPanel.Controls)
         addin.Visible = addin.Equals(control);
      addinSplitter.Visible = true;
      addinPanel.Visible = true;
      addinPanel.Height = control.Height;
      control.Focus();
    }
    private void HideAddin()
    {
      addinSplitter.Visible = false;
      addinPanel.Visible = false;
    }
    private void AddinClosing(object sender, System.EventArgs e)
    {
      HideAddin();
      EditSetBase est = sender as EditSetBase;
      est.Closing -= new EventHandler(AddinClosing);
    }
    private void GoToClick(object sender, System.EventArgs e)
    {
      SetEditActiveControl();
      TextEditorControl editor = ActiveControl as TextEditorControl;
      if (editor != null)
      {
        EditSetBase est = editor.Tag as EditSetBase;
        est.Closing += new EventHandler(AddinClosing);
        GoToAddin.EditSet = est;
        toolTips.SetToolTip(GoToAddin.CloseButton, AddinHint(est.tabPage.Text));
        SetButton(GoToAddin.CloseButton, est.tabPage.Text);
        ShowAddin(GoToAddin);
      }
    }
    private void CompileClick(object sender, System.EventArgs e)
    {
      LogVerbose ="Compile";
      CloseAllViews(sender, e);
      SyncEditTabToSourceList();
      bool clearError = true;
      if (sourceList.SelectedIndex != -1)
      {
        Source source = sourceList.SelectedItem as Source;
        if (source != null)
        {
          SourceListSelectedIndexChanged(sender, e);
          CheckSaveEdit(source);
          RunCompile(source, ref clearError);
          project.Save();
          SourceListSelectedIndexChanged(sender, e);
        }
      }
    }
    private void CompileViewClick(object sender, System.EventArgs e)
    {
      LogVerbose ="CompileViewClick NOT YET IMPLEMENTED";
    }
    private void CompileOutOfDateClick(object sender, System.EventArgs e)
    {
      LogVerbose ="CompileOutOfDate";
      CloseAllViews(sender, e);
      bool clearError = true;
      foreach (Source source in sourceList.Items)
      {
        CheckSaveEdit(source);
        if (source.OutOfDate)
          RunCompile(source, ref clearError);
      }
      project.Save();
    }
    private void CompileAllClick(object sender, System.EventArgs e)
    {
      LogVerbose ="CompileAll";
      CloseAllViews(sender, e);
      bool clearError = true;
      foreach (Source source in sourceList.Items)
      {
        CheckSaveEdit(source);
        RunCompile(source, ref clearError);
      }
      project.Save();
    }
    private void PrintSetupClick(object sender, System.EventArgs e)
    {
      printDialog.ShowDialog();
    }
    private TextEditorControl printEditor;
    private int currLine=0;
    private void PrintDocumentPrintPage(object sender, System.Drawing.Printing.PrintPageEventArgs e)
    {
      string x = printEditor.Document.TextContent;
      string[] lines = x.Split(new char[] {'\n'});
      TextArea area = printEditor.ActiveTextAreaControl.TextArea;
      Font printFont = printEditor.Font;
      float linesPerPage = 0;
      float yPos =  0;
      int count = 0;
      float leftMargin = e.MarginBounds.Left;
      float topMargin = e.MarginBounds.Top;
      String line=null;
      // Calculate the number of lines per page.
      linesPerPage = e.MarginBounds.Height / printFont.GetHeight(e.Graphics);
      // Iterate over the file, printing each line.
      while (count < linesPerPage && currLine < lines.Length) 
      {
        line = lines[currLine++].TrimEnd(new char[] {'\t',' ','\r'});
        yPos = topMargin + (count * printFont.GetHeight(e.Graphics));
        e.Graphics.DrawString (line, printFont, Brushes.Black, 
          leftMargin, yPos, new StringFormat());
        count++;
      }
      // If more lines exist, print another page.
      e.HasMorePages = currLine < lines.Length;
    }
    private void PrintClick(object sender, System.EventArgs e)
    {
      SetEditActiveControl();
      printEditor = ActiveControl as TextEditorControl;
      if (printEditor != null)
      {
        currLine = 0;
        printDocument.Print();
      }
    }
    private void AboutClick(object sender, System.EventArgs e)
    {
      AboutForm about = new AboutForm();
      about.ShowDialog();
    }
    private void ExitClick(object sender, System.EventArgs e)
    {
      Close();
    }
    private string ChangeExt(string file, string ext)
    {
      FileInfo info = new FileInfo(file);
      return file.Substring(0, file.LastIndexOf(info.Extension))+ext;
    }
    private void HelpContentsClick(object sender, System.EventArgs e)
    {
      Help.ShowHelp(this, helpProvider.HelpNamespace);
    }
    private void AddinDoubleClick(object sender, System.EventArgs e)
    {
      HideAddin();
    }
  }
}
