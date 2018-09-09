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
/// $Date: 2004/11/17 08:50:31 $
/// $Revision: 411.2 $ // YMM.Revision
/// ------------------------------------------------------------------

using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Reflection;

namespace Bbd.AnyDB
{
	/// <summary>
	/// Summary description for AboutForm.
	/// </summary>
	public class AboutForm : System.Windows.Forms.Form
	{
    private System.Windows.Forms.PictureBox pictureBox;
    private System.Windows.Forms.Label nameLabel;
    private System.Windows.Forms.Label companyLabel;
    private System.Windows.Forms.Label versionLabel;
    private System.Windows.Forms.LinkLabel linkLabel1;
    private System.Windows.Forms.ToolTip toolTip1;
    private System.Windows.Forms.Button button1;
    private System.ComponentModel.IContainer components;

		public AboutForm()
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
				if(components != null)
				{
					components.Dispose();
				}
			}
			base.Dispose( disposing );
		}
		#region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
      this.components = new System.ComponentModel.Container();
      System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(AboutForm));
      this.pictureBox = new System.Windows.Forms.PictureBox();
      this.nameLabel = new System.Windows.Forms.Label();
      this.companyLabel = new System.Windows.Forms.Label();
      this.versionLabel = new System.Windows.Forms.Label();
      this.linkLabel1 = new System.Windows.Forms.LinkLabel();
      this.toolTip1 = new System.Windows.Forms.ToolTip(this.components);
      this.button1 = new System.Windows.Forms.Button();
      this.SuspendLayout();
      // 
      // pictureBox
      // 
      this.pictureBox.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox.Image")));
      this.pictureBox.Location = new System.Drawing.Point(40, 184);
      this.pictureBox.Name = "pictureBox";
      this.pictureBox.Size = new System.Drawing.Size(192, 160);
      this.pictureBox.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
      this.pictureBox.TabIndex = 0;
      this.pictureBox.TabStop = false;
      // 
      // nameLabel
      // 
      this.nameLabel.AutoSize = true;
      this.nameLabel.BackColor = System.Drawing.Color.Transparent;
      this.nameLabel.Font = new System.Drawing.Font("Palatino Linotype", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.nameLabel.ForeColor = System.Drawing.Color.Wheat;
      this.nameLabel.Location = new System.Drawing.Point(16, 72);
      this.nameLabel.Name = "nameLabel";
      this.nameLabel.Size = new System.Drawing.Size(95, 25);
      this.nameLabel.TabIndex = 1;
      this.nameLabel.Text = "Any DB Ide";
      // 
      // companyLabel
      // 
      this.companyLabel.AutoSize = true;
      this.companyLabel.BackColor = System.Drawing.Color.Transparent;
      this.companyLabel.Font = new System.Drawing.Font("Palatino Linotype", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.companyLabel.ForeColor = System.Drawing.Color.Wheat;
      this.companyLabel.Location = new System.Drawing.Point(16, 120);
      this.companyLabel.Name = "companyLabel";
      this.companyLabel.Size = new System.Drawing.Size(228, 25);
      this.companyLabel.TabIndex = 2;
      this.companyLabel.Text = "Barone, Budge and Dominick";
      // 
      // versionLabel
      // 
      this.versionLabel.AutoSize = true;
      this.versionLabel.BackColor = System.Drawing.Color.Transparent;
      this.versionLabel.Font = new System.Drawing.Font("Palatino Linotype", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.versionLabel.ForeColor = System.Drawing.Color.Wheat;
      this.versionLabel.Location = new System.Drawing.Point(16, 96);
      this.versionLabel.Name = "versionLabel";
      this.versionLabel.Size = new System.Drawing.Size(95, 25);
      this.versionLabel.TabIndex = 3;
      this.versionLabel.Text = "Any DB Ide";
      // 
      // linkLabel1
      // 
      this.linkLabel1.AutoSize = true;
      this.linkLabel1.BackColor = System.Drawing.Color.Transparent;
      this.linkLabel1.LinkColor = System.Drawing.Color.FromArgb(((System.Byte)(255)), ((System.Byte)(192)), ((System.Byte)(128)));
      this.linkLabel1.Location = new System.Drawing.Point(456, 376);
      this.linkLabel1.Name = "linkLabel1";
      this.linkLabel1.Size = new System.Drawing.Size(116, 18);
      this.linkLabel1.TabIndex = 4;
      this.linkLabel1.TabStop = true;
      this.linkLabel1.Text = "www.freefoto.com";
      // 
      // button1
      // 
      this.button1.BackColor = System.Drawing.Color.Transparent;
      this.button1.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
      this.button1.ForeColor = System.Drawing.Color.White;
      this.button1.Location = new System.Drawing.Point(568, 8);
      this.button1.Name = "button1";
      this.button1.Size = new System.Drawing.Size(16, 16);
      this.button1.TabIndex = 5;
      this.button1.Text = "X";
      this.button1.Click += new System.EventHandler(this.AboutFormClick);
      // 
      // AboutForm
      // 
      this.AutoScaleBaseSize = new System.Drawing.Size(7, 15);
      this.BackColor = System.Drawing.SystemColors.Control;
      this.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("$this.BackgroundImage")));
      this.ClientSize = new System.Drawing.Size(594, 399);
      this.Controls.Add(this.button1);
      this.Controls.Add(this.linkLabel1);
      this.Controls.Add(this.versionLabel);
      this.Controls.Add(this.companyLabel);
      this.Controls.Add(this.nameLabel);
      this.Controls.Add(this.pictureBox);
      this.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
      this.MaximizeBox = false;
      this.MinimizeBox = false;
      this.Name = "AboutForm";
      this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
      this.Text = "About";
      this.toolTip1.SetToolTip(this, "Click mouse onto close");
      this.KeyDown += new System.Windows.Forms.KeyEventHandler(this.AboutFormKeyDown);
      this.Click += new System.EventHandler(this.AboutFormClick);
      this.Load += new System.EventHandler(this.AboutFormLoad);
      this.ResumeLayout(false);

    }
		#endregion
    private void AboutFormLoad(object sender, System.EventArgs e)
    {
      Assembly assembly = Assembly.GetExecutingAssembly();
      versionLabel.Text = "Version: "+assembly.GetName().Version.ToString();
    }
    private void AboutFormClick(object sender, System.EventArgs e)
    {
      Close();
    }
    private void AboutFormKeyDown(object sender, System.Windows.Forms.KeyEventArgs e)
    {
      Close();
    }

	}
}
