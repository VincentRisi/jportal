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
using ICSharpCode.TextEditor;
using ICSharpCode.TextEditor.Document;

namespace Bbd.AnyDB
{
	/// <summary>
	/// Summary description for FindUC.
	/// </summary>
	public class FindUC : System.Windows.Forms.UserControl
	{
    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.Button findButton;
    private System.Windows.Forms.TextBox findTextBox;
    private System.Windows.Forms.CheckBox matchCaseCheckBox;
    private System.Windows.Forms.CheckBox matchWholeWordCheckBox;
    private System.Windows.Forms.CheckBox searchUpCheckBox;
    private System.Windows.Forms.CheckBox regExCheckBox;
    private System.Windows.Forms.Button closeButton;
		/// <summary> 
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public FindUC()
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
      this.findButton = new System.Windows.Forms.Button();
      this.findTextBox = new System.Windows.Forms.TextBox();
      this.matchCaseCheckBox = new System.Windows.Forms.CheckBox();
      this.matchWholeWordCheckBox = new System.Windows.Forms.CheckBox();
      this.searchUpCheckBox = new System.Windows.Forms.CheckBox();
      this.regExCheckBox = new System.Windows.Forms.CheckBox();
      this.closeButton = new System.Windows.Forms.Button();
      this.SuspendLayout();
      // 
      // label1
      // 
      this.label1.AutoSize = true;
      this.label1.Location = new System.Drawing.Point(8, 8);
      this.label1.Name = "label1";
      this.label1.Size = new System.Drawing.Size(26, 16);
      this.label1.TabIndex = 7;
      this.label1.Text = "Find";
      // 
      // findButton
      // 
      this.findButton.Enabled = false;
      this.findButton.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
      this.findButton.Location = new System.Drawing.Point(320, 8);
      this.findButton.Name = "findButton";
      this.findButton.TabIndex = 1;
      this.findButton.Text = "Find Next";
      this.findButton.Click += new System.EventHandler(this.FindClick);
      // 
      // findTextBox
      // 
      this.findTextBox.Location = new System.Drawing.Point(48, 8);
      this.findTextBox.Name = "findTextBox";
      this.findTextBox.Size = new System.Drawing.Size(256, 20);
      this.findTextBox.TabIndex = 0;
      this.findTextBox.Text = "";
      this.findTextBox.TextChanged += new System.EventHandler(this.FindTextChanged);
      // 
      // matchCaseCheckBox
      // 
      this.matchCaseCheckBox.Location = new System.Drawing.Point(8, 40);
      this.matchCaseCheckBox.Name = "matchCaseCheckBox";
      this.matchCaseCheckBox.Size = new System.Drawing.Size(88, 24);
      this.matchCaseCheckBox.TabIndex = 3;
      this.matchCaseCheckBox.Text = "Match case";
      // 
      // matchWholeWordCheckBox
      // 
      this.matchWholeWordCheckBox.Location = new System.Drawing.Point(96, 40);
      this.matchWholeWordCheckBox.Name = "matchWholeWordCheckBox";
      this.matchWholeWordCheckBox.Size = new System.Drawing.Size(120, 24);
      this.matchWholeWordCheckBox.TabIndex = 4;
      this.matchWholeWordCheckBox.Text = "Match whole word";
      // 
      // searchUpCheckBox
      // 
      this.searchUpCheckBox.Location = new System.Drawing.Point(216, 40);
      this.searchUpCheckBox.Name = "searchUpCheckBox";
      this.searchUpCheckBox.Size = new System.Drawing.Size(78, 24);
      this.searchUpCheckBox.TabIndex = 5;
      this.searchUpCheckBox.Text = "Search up";
      // 
      // regExCheckBox
      // 
      this.regExCheckBox.Enabled = false;
      this.regExCheckBox.Location = new System.Drawing.Point(296, 40);
      this.regExCheckBox.Name = "regExCheckBox";
      this.regExCheckBox.Size = new System.Drawing.Size(128, 24);
      this.regExCheckBox.TabIndex = 6;
      this.regExCheckBox.Text = "Regular Expression";
      // 
      // closeButton
      // 
      this.closeButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
      this.closeButton.DialogResult = System.Windows.Forms.DialogResult.Cancel;
      this.closeButton.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
      this.closeButton.Location = new System.Drawing.Point(464, 40);
      this.closeButton.Name = "closeButton";
      this.closeButton.Size = new System.Drawing.Size(152, 23);
      this.closeButton.TabIndex = 2;
      this.closeButton.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
      // 
      // FindUC
      // 
      this.Controls.Add(this.closeButton);
      this.Controls.Add(this.regExCheckBox);
      this.Controls.Add(this.searchUpCheckBox);
      this.Controls.Add(this.matchWholeWordCheckBox);
      this.Controls.Add(this.matchCaseCheckBox);
      this.Controls.Add(this.label1);
      this.Controls.Add(this.findButton);
      this.Controls.Add(this.findTextBox);
      this.Name = "FindUC";
      this.Size = new System.Drawing.Size(616, 64);
      this.ResumeLayout(false);

    }
		#endregion

    private static FindUC instance;
    public static FindUC Instance
    {
      get 
      {
        if (instance == null)
          instance = new FindUC();
        return instance;
      }
    }
    private EditSetBase editSet;
    public EditSetBase EditSet 
    { 
      set 
      { 
        editSet = value; 
      }
    }

    private void FindClick(object sender, System.EventArgs e)
    {
      SearchReplace sr = new SearchReplace(editSet);
      sr.Find(findTextBox.Text, null, matchCaseCheckBox.Checked,
        matchWholeWordCheckBox.Checked,
        searchUpCheckBox.Checked,
        regExCheckBox.Checked);
    }

    private void FindTextChanged(object sender, System.EventArgs e)
    {
      findButton.Enabled = findTextBox.Text.Length > 0;
    }
    public Button CloseButton { get { return closeButton; }}
  }
}
