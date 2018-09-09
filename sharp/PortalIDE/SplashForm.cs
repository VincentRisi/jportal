using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;

namespace Bbd.AnyDB
{
	/// <summary>
	/// Summary description for SplashForm.
	/// </summary>
	public class SplashForm : System.Windows.Forms.Form
	{
    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.Label infoLabel;
    private System.Windows.Forms.LinkLabel linkLabel1;
    private System.Windows.Forms.Label label2;
    private System.Windows.Forms.Label label3;
    private System.Windows.Forms.Label label4;
    private System.Windows.Forms.Label label5;
    private System.Windows.Forms.Label label6;
    private System.Windows.Forms.Label label7;
    private System.Windows.Forms.Label label8;
    private System.Windows.Forms.LinkLabel linkLabel2;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public SplashForm()
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
      System.Resources.ResourceManager resources = new System.Resources.ResourceManager(typeof(SplashForm));
      this.label1 = new System.Windows.Forms.Label();
      this.infoLabel = new System.Windows.Forms.Label();
      this.linkLabel1 = new System.Windows.Forms.LinkLabel();
      this.label2 = new System.Windows.Forms.Label();
      this.label3 = new System.Windows.Forms.Label();
      this.label4 = new System.Windows.Forms.Label();
      this.label5 = new System.Windows.Forms.Label();
      this.label6 = new System.Windows.Forms.Label();
      this.label7 = new System.Windows.Forms.Label();
      this.label8 = new System.Windows.Forms.Label();
      this.linkLabel2 = new System.Windows.Forms.LinkLabel();
      this.SuspendLayout();
      // 
      // label1
      // 
      this.label1.AutoSize = true;
      this.label1.BackColor = System.Drawing.Color.Transparent;
      this.label1.Font = new System.Drawing.Font("Palatino Linotype", 20.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.label1.ForeColor = System.Drawing.Color.SeaShell;
      this.label1.Location = new System.Drawing.Point(24, 16);
      this.label1.Name = "label1";
      this.label1.Size = new System.Drawing.Size(311, 40);
      this.label1.TabIndex = 0;
      this.label1.Text = "Any DB Ide version 504";
      // 
      // infoLabel
      // 
      this.infoLabel.AutoSize = true;
      this.infoLabel.BackColor = System.Drawing.Color.Transparent;
      this.infoLabel.Font = new System.Drawing.Font("Microsoft Sans Serif", 9F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.infoLabel.ForeColor = System.Drawing.Color.SeaShell;
      this.infoLabel.Location = new System.Drawing.Point(24, 344);
      this.infoLabel.Name = "infoLabel";
      this.infoLabel.Size = new System.Drawing.Size(16, 17);
      this.infoLabel.TabIndex = 1;
      this.infoLabel.Text = "...";
      // 
      // linkLabel1
      // 
      this.linkLabel1.AutoSize = true;
      this.linkLabel1.BackColor = System.Drawing.Color.Transparent;
      this.linkLabel1.LinkColor = System.Drawing.Color.FromArgb(((System.Byte)(255)), ((System.Byte)(192)), ((System.Byte)(128)));
      this.linkLabel1.Location = new System.Drawing.Point(448, 368);
      this.linkLabel1.Name = "linkLabel1";
      this.linkLabel1.Size = new System.Drawing.Size(133, 24);
      this.linkLabel1.TabIndex = 2;
      this.linkLabel1.TabStop = true;
      this.linkLabel1.Text = "www.freefoto.com";
      // 
      // label2
      // 
      this.label2.AutoSize = true;
      this.label2.BackColor = System.Drawing.Color.Transparent;
      this.label2.Font = new System.Drawing.Font("Palatino Linotype", 11.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.label2.ForeColor = System.Drawing.Color.SeaShell;
      this.label2.Location = new System.Drawing.Point(32, 56);
      this.label2.Name = "label2";
      this.label2.Size = new System.Drawing.Size(368, 24);
      this.label2.TabIndex = 3;
      this.label2.Text = "Copyright (c) 1996, 2004 Vincent Risi in Association";
      // 
      // label3
      // 
      this.label3.AutoSize = true;
      this.label3.BackColor = System.Drawing.Color.Transparent;
      this.label3.Font = new System.Drawing.Font("Palatino Linotype", 11.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.label3.ForeColor = System.Drawing.Color.SeaShell;
      this.label3.Location = new System.Drawing.Point(32, 80);
      this.label3.Name = "label3";
      this.label3.Size = new System.Drawing.Size(347, 24);
      this.label3.TabIndex = 4;
      this.label3.Text = "                         with Barone Budge and Dominick ";
      this.label3.Click += new System.EventHandler(this.label3_Click);
      // 
      // label4
      // 
      this.label4.AutoSize = true;
      this.label4.BackColor = System.Drawing.Color.Transparent;
      this.label4.Font = new System.Drawing.Font("Palatino Linotype", 11.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.label4.ForeColor = System.Drawing.Color.SeaShell;
      this.label4.Location = new System.Drawing.Point(32, 104);
      this.label4.Name = "label4";
      this.label4.Size = new System.Drawing.Size(141, 24);
      this.label4.TabIndex = 5;
      this.label4.Text = "All rights reserved.";
      // 
      // label5
      // 
      this.label5.AutoSize = true;
      this.label5.BackColor = System.Drawing.Color.Transparent;
      this.label5.Font = new System.Drawing.Font("Palatino Linotype", 11.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.label5.ForeColor = System.Drawing.Color.SeaShell;
      this.label5.Location = new System.Drawing.Point(32, 128);
      this.label5.Name = "label5";
      this.label5.Size = new System.Drawing.Size(469, 24);
      this.label5.TabIndex = 6;
      this.label5.Text = "This program and the accompanying materials are made available";
      // 
      // label6
      // 
      this.label6.AutoSize = true;
      this.label6.BackColor = System.Drawing.Color.Transparent;
      this.label6.Font = new System.Drawing.Font("Palatino Linotype", 11.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.label6.ForeColor = System.Drawing.Color.SeaShell;
      this.label6.Location = new System.Drawing.Point(32, 152);
      this.label6.Name = "label6";
      this.label6.Size = new System.Drawing.Size(373, 24);
      this.label6.TabIndex = 7;
      this.label6.Text = "under the terms of the Common Public License v1.0 ";
      // 
      // label7
      // 
      this.label7.AutoSize = true;
      this.label7.BackColor = System.Drawing.Color.Transparent;
      this.label7.Font = new System.Drawing.Font("Palatino Linotype", 11.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.label7.ForeColor = System.Drawing.Color.SeaShell;
      this.label7.Location = new System.Drawing.Point(32, 176);
      this.label7.Name = "label7";
      this.label7.Size = new System.Drawing.Size(469, 24);
      this.label7.TabIndex = 8;
      this.label7.Text = "This program and the accompanying materials are made available";
      // 
      // label8
      // 
      this.label8.AutoSize = true;
      this.label8.BackColor = System.Drawing.Color.Transparent;
      this.label8.Font = new System.Drawing.Font("Palatino Linotype", 11.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.label8.ForeColor = System.Drawing.Color.SeaShell;
      this.label8.Location = new System.Drawing.Point(32, 200);
      this.label8.Name = "label8";
      this.label8.Size = new System.Drawing.Size(394, 24);
      this.label8.TabIndex = 9;
      this.label8.Text = "which accompanies this distribution and is available at";
      // 
      // linkLabel2
      // 
      this.linkLabel2.AutoSize = true;
      this.linkLabel2.BackColor = System.Drawing.Color.Transparent;
      this.linkLabel2.Font = new System.Drawing.Font("Palatino Linotype", 11.25F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Italic))), System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.linkLabel2.LinkColor = System.Drawing.Color.FromArgb(((System.Byte)(255)), ((System.Byte)(192)), ((System.Byte)(128)));
      this.linkLabel2.Location = new System.Drawing.Point(32, 224);
      this.linkLabel2.Name = "linkLabel2";
      this.linkLabel2.Size = new System.Drawing.Size(251, 24);
      this.linkLabel2.TabIndex = 10;
      this.linkLabel2.TabStop = true;
      this.linkLabel2.Text = "www.eclipse.org/legal/cpl-v10.html";
      // 
      // SplashForm
      // 
      this.AutoScaleBaseSize = new System.Drawing.Size(7, 21);
      this.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("$this.BackgroundImage")));
      this.ClientSize = new System.Drawing.Size(600, 400);
      this.Controls.Add(this.linkLabel2);
      this.Controls.Add(this.label8);
      this.Controls.Add(this.label7);
      this.Controls.Add(this.label6);
      this.Controls.Add(this.label5);
      this.Controls.Add(this.label4);
      this.Controls.Add(this.label3);
      this.Controls.Add(this.label2);
      this.Controls.Add(this.linkLabel1);
      this.Controls.Add(this.infoLabel);
      this.Controls.Add(this.label1);
      this.Font = new System.Drawing.Font("Palatino Linotype", 11.25F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Italic))), System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
      this.MaximizeBox = false;
      this.MinimizeBox = false;
      this.Name = "SplashForm";
      this.Opacity = 0.95;
      this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
      this.Text = "SplashForm";
      this.ResumeLayout(false);

    }
		#endregion

    private void label3_Click(object sender, System.EventArgs e)
    {
    
    }
  
    public string Info
    {
      set { infoLabel.Text = value; Refresh(); }
    }

	}
}
