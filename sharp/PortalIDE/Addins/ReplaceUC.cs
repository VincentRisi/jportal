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
/// $Date: 2004/11/17 08:50:33 $
/// $Revision: 1.1 $ // YMM.Revision
/// ------------------------------------------------------------------

using System;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Windows.Forms;

namespace Bbd.AnyDB
{
	/// <summary>
	/// Summary description for ReplaceUC.
	/// </summary>
	public class ReplaceUC : System.Windows.Forms.UserControl
	{
    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.TextBox findTextBox;
    private System.Windows.Forms.CheckBox matchCaseCheckBox;
    private System.Windows.Forms.CheckBox matchWholeWordCheckBox;
    private System.Windows.Forms.CheckBox searchUpCheckBox;
    private System.Windows.Forms.Label label2;
    private System.Windows.Forms.TextBox replaceTextBox;
    private System.Windows.Forms.Button replaceAllButton;
    private System.Windows.Forms.Button replaceButton;
    private System.Windows.Forms.Button closeButton;
    private System.Windows.Forms.CheckBox regExCheckBox;
		/// <summary> 
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public ReplaceUC()
		{
			// This call is required by the Windows.Forms Form Designer.
			InitializeComponent();

			// TODO: Add any initialization after the InitializeComponent call

		}

		/// <summary> 
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if( disposing )
			{
				if(components != null)
				{
					components.Dispose();
				}
			}
			base.Dispose( disposing );
		}

		#region Component Designer generated code
		/// <summary> 
		/// Required method for Designer support - do not modify 
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
      this.label1 = new System.Windows.Forms.Label();
      this.findTextBox = new System.Windows.Forms.TextBox();
      this.matchCaseCheckBox = new System.Windows.Forms.CheckBox();
      this.matchWholeWordCheckBox = new System.Windows.Forms.CheckBox();
      this.searchUpCheckBox = new System.Windows.Forms.CheckBox();
      this.regExCheckBox = new System.Windows.Forms.CheckBox();
      this.label2 = new System.Windows.Forms.Label();
      this.replaceTextBox = new System.Windows.Forms.TextBox();
      this.replaceAllButton = new System.Windows.Forms.Button();
      this.replaceButton = new System.Windows.Forms.Button();
      this.closeButton = new System.Windows.Forms.Button();
      this.SuspendLayout();
      // 
      // label1
      // 
      this.label1.AutoSize = true;
      this.label1.Location = new System.Drawing.Point(8, 16);
      this.label1.Name = "label1";
      this.label1.Size = new System.Drawing.Size(26, 16);
      this.label1.TabIndex = 9;
      this.label1.Text = "Find";
      // 
      // findTextBox
      // 
      this.findTextBox.Location = new System.Drawing.Point(72, 8);
      this.findTextBox.Name = "findTextBox";
      this.findTextBox.Size = new System.Drawing.Size(232, 20);
      this.findTextBox.TabIndex = 0;
      this.findTextBox.Text = "";
      this.findTextBox.TextChanged += new System.EventHandler(this.TextBoxTextChanged);
      // 
      // matchCaseCheckBox
      // 
      this.matchCaseCheckBox.Location = new System.Drawing.Point(8, 64);
      this.matchCaseCheckBox.Name = "matchCaseCheckBox";
      this.matchCaseCheckBox.Size = new System.Drawing.Size(88, 24);
      this.matchCaseCheckBox.TabIndex = 5;
      this.matchCaseCheckBox.Text = "Match case";
      // 
      // matchWholeWordCheckBox
      // 
      this.matchWholeWordCheckBox.Location = new System.Drawing.Point(96, 64);
      this.matchWholeWordCheckBox.Name = "matchWholeWordCheckBox";
      this.matchWholeWordCheckBox.Size = new System.Drawing.Size(120, 24);
      this.matchWholeWordCheckBox.TabIndex = 6;
      this.matchWholeWordCheckBox.Text = "Match whole word";
      // 
      // searchUpCheckBox
      // 
      this.searchUpCheckBox.Location = new System.Drawing.Point(216, 64);
      this.searchUpCheckBox.Name = "searchUpCheckBox";
      this.searchUpCheckBox.Size = new System.Drawing.Size(78, 24);
      this.searchUpCheckBox.TabIndex = 7;
      this.searchUpCheckBox.Text = "Search up";
      // 
      // regExCheckBox
      // 
      this.regExCheckBox.Enabled = false;
      this.regExCheckBox.Location = new System.Drawing.Point(296, 64);
      this.regExCheckBox.Name = "regExCheckBox";
      this.regExCheckBox.Size = new System.Drawing.Size(128, 24);
      this.regExCheckBox.TabIndex = 8;
      this.regExCheckBox.Text = "Regular Expression";
      // 
      // label2
      // 
      this.label2.AutoSize = true;
      this.label2.Location = new System.Drawing.Point(8, 40);
      this.label2.Name = "label2";
      this.label2.Size = new System.Drawing.Size(46, 16);
      this.label2.TabIndex = 10;
      this.label2.Text = "Replace";
      // 
      // replaceTextBox
      // 
      this.replaceTextBox.Location = new System.Drawing.Point(72, 32);
      this.replaceTextBox.Name = "replaceTextBox";
      this.replaceTextBox.Size = new System.Drawing.Size(232, 20);
      this.replaceTextBox.TabIndex = 1;
      this.replaceTextBox.Text = "";
      // 
      // replaceAllButton
      // 
      this.replaceAllButton.Enabled = false;
      this.replaceAllButton.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
      this.replaceAllButton.Location = new System.Drawing.Point(328, 32);
      this.replaceAllButton.Name = "replaceAllButton";
      this.replaceAllButton.TabIndex = 3;
      this.replaceAllButton.Text = "Replace All";
      this.replaceAllButton.Click += new System.EventHandler(this.ReplaceAllClick);
      // 
      // replaceButton
      // 
      this.replaceButton.Enabled = false;
      this.replaceButton.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
      this.replaceButton.Location = new System.Drawing.Point(328, 8);
      this.replaceButton.Name = "replaceButton";
      this.replaceButton.TabIndex = 2;
      this.replaceButton.Text = "Replace";
      this.replaceButton.Click += new System.EventHandler(this.ReplaceClick);
      // 
      // closeButton
      // 
      this.closeButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
      this.closeButton.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
      this.closeButton.Location = new System.Drawing.Point(472, 64);
      this.closeButton.Name = "closeButton";
      this.closeButton.Size = new System.Drawing.Size(152, 23);
      this.closeButton.TabIndex = 4;
      this.closeButton.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
      // 
      // ReplaceUC
      // 
      this.Controls.Add(this.closeButton);
      this.Controls.Add(this.replaceAllButton);
      this.Controls.Add(this.replaceButton);
      this.Controls.Add(this.label2);
      this.Controls.Add(this.replaceTextBox);
      this.Controls.Add(this.regExCheckBox);
      this.Controls.Add(this.searchUpCheckBox);
      this.Controls.Add(this.matchWholeWordCheckBox);
      this.Controls.Add(this.matchCaseCheckBox);
      this.Controls.Add(this.label1);
      this.Controls.Add(this.findTextBox);
      this.Name = "ReplaceUC";
      this.Size = new System.Drawing.Size(624, 88);
      this.ResumeLayout(false);

    }
		#endregion

    private static ReplaceUC instance;
    public static ReplaceUC Instance
    {
      get 
      {
        if (instance == null)
          instance = new ReplaceUC();
        return instance;
      }
    }
    private EditSetBase editSet;

    private void TextBoxTextChanged(object sender, System.EventArgs e)
    {
      bool enable = findTextBox.TextLength > 0;
      replaceAllButton.Enabled = enable;
      replaceButton.Enabled = enable;
    }
    private bool Replace()
    {
      SearchReplace sr = new SearchReplace(editSet);
      return sr.Find(findTextBox.Text, replaceTextBox.Text, matchCaseCheckBox.Checked,
        matchWholeWordCheckBox.Checked, searchUpCheckBox.Checked, regExCheckBox.Checked);
    }
    private void ReplaceClick(object sender, System.EventArgs e)
    {
      Replace();
    }
    private void ReplaceAllClick(object sender, System.EventArgs e)
    {
      while (Replace())
        ;
    }
    public EditSetBase EditSet { set { editSet = value; }}
    public Button CloseButton { get { return closeButton; }}
  }
}
