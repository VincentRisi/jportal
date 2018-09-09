using System;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Windows.Forms;

namespace Bbd.AnyDB
{
	/// <summary>
	/// Summary description for ShowErrorUC.
	/// </summary>
	public class ShowErrorUC : System.Windows.Forms.UserControl
	{
    private System.Windows.Forms.Button closeButton;
    private System.Windows.Forms.RichTextBox logRichTextBox;
		/// <summary> 
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public ShowErrorUC()
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
      this.closeButton = new System.Windows.Forms.Button();
      this.logRichTextBox = new System.Windows.Forms.RichTextBox();
      this.SuspendLayout();
      // 
      // closeButton
      // 
      this.closeButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
      this.closeButton.DialogResult = System.Windows.Forms.DialogResult.Cancel;
      this.closeButton.FlatStyle = System.Windows.Forms.FlatStyle.Popup;
      this.closeButton.Location = new System.Drawing.Point(540, 76);
      this.closeButton.Name = "closeButton";
      this.closeButton.Size = new System.Drawing.Size(152, 23);
      this.closeButton.TabIndex = 3;
      this.closeButton.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
      // 
      // logRichTextBox
      // 
      this.logRichTextBox.Dock = System.Windows.Forms.DockStyle.Fill;
      this.logRichTextBox.Location = new System.Drawing.Point(0, 0);
      this.logRichTextBox.Name = "logRichTextBox";
      this.logRichTextBox.Size = new System.Drawing.Size(692, 100);
      this.logRichTextBox.TabIndex = 4;
      this.logRichTextBox.Text = "";
      // 
      // ShowErrorUC
      // 
      this.Controls.Add(this.closeButton);
      this.Controls.Add(this.logRichTextBox);
      this.Name = "ShowErrorUC";
      this.Size = new System.Drawing.Size(692, 100);
      this.ResumeLayout(false);

    }
		#endregion
    private static ShowErrorUC instance;
    public static ShowErrorUC Instance
    {
      get 
      {
        if (instance == null)
          instance = new ShowErrorUC();
        return instance;
      }
    }
    public Button CloseButton { get { return closeButton; }}
    public RichTextBox LogRichTextBox { get { return logRichTextBox; }}
	}
}
