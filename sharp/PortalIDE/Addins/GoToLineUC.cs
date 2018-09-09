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
	/// Summary description for GoToLineUC.
	/// </summary>
	public class GoToLineUC : System.Windows.Forms.UserControl
	{
    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.NumericUpDown lineNo;
    private System.Windows.Forms.Button gotoButton;
    private System.Windows.Forms.Button closeButton;
		/// <summary> 
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public GoToLineUC()
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
      this.lineNo = new System.Windows.Forms.NumericUpDown();
      this.gotoButton = new System.Windows.Forms.Button();
      this.closeButton = new System.Windows.Forms.Button();
      ((System.ComponentModel.ISupportInitialize)(this.lineNo)).BeginInit();
      this.SuspendLayout();
      // 
      // label1
      // 
      this.label1.AutoSize = true;
      this.label1.Location = new System.Drawing.Point(16, 4);
      this.label1.Name = "label1";
      this.label1.Size = new System.Drawing.Size(26, 16);
      this.label1.TabIndex = 3;
      this.label1.Text = "Line";
      // 
      // lineNo
      // 
      this.lineNo.Location = new System.Drawing.Point(72, 4);
      this.lineNo.Maximum = new System.Decimal(new int[] {
                                                           1000,
                                                           0,
                                                           0,
                                                           0});
      this.lineNo.Name = "lineNo";
      this.lineNo.Size = new System.Drawing.Size(112, 20);
      this.lineNo.TabIndex = 0;
      // 
      // gotoButton
      // 
      this.gotoButton.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
      this.gotoButton.Location = new System.Drawing.Point(208, 4);
      this.gotoButton.Name = "gotoButton";
      this.gotoButton.TabIndex = 1;
      this.gotoButton.Text = "GoTo";
      this.gotoButton.Click += new System.EventHandler(this.GoToButtonClick);
      // 
      // closeButton
      // 
      this.closeButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
      this.closeButton.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
      this.closeButton.Location = new System.Drawing.Point(376, 8);
      this.closeButton.Name = "closeButton";
      this.closeButton.Size = new System.Drawing.Size(152, 23);
      this.closeButton.TabIndex = 2;
      this.closeButton.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
      // 
      // GoToLineUC
      // 
      this.Controls.Add(this.closeButton);
      this.Controls.Add(this.gotoButton);
      this.Controls.Add(this.lineNo);
      this.Controls.Add(this.label1);
      this.Name = "GoToLineUC";
      this.Size = new System.Drawing.Size(528, 32);
      ((System.ComponentModel.ISupportInitialize)(this.lineNo)).EndInit();
      this.ResumeLayout(false);

    }
		#endregion
    private static GoToLineUC instance;
    public static GoToLineUC Instance
    {
      get 
      {
        if (instance == null)
          instance = new GoToLineUC();
        return instance;
      }
    }
    private EditSetBase editSet;
    private void GoToButtonClick(object sender, System.EventArgs e)
    {
      int n = decimal.ToInt32(lineNo.Value);
      if (n > 0)
      {
        Caret caret = editSet.editor.ActiveTextAreaControl.Caret;
        TextView view = editSet.editor.ActiveTextAreaControl.TextArea.TextView;
        IDocument document = editSet.editor.Document;
        caret.Position = new Point(0, n-1);
        view.FirstVisibleLine = editSet.RationalStart(caret.Line, 8);
      }
      editSet.SetEditorActive();
    }
    public EditSetBase EditSet { set { editSet = value; }}
    public Button CloseButton { get { return closeButton; }}
  }
}
