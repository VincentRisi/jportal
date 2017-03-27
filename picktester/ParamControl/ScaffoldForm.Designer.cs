namespace bbd.ParamControl
{
  partial class ScaffoldForm
  {
    /// <summary>
    /// Required designer variable.
    /// </summary>
    private System.ComponentModel.IContainer components = null;

    /// <summary>
    /// Clean up any resources being used.
    /// </summary>
    /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
    protected override void Dispose(bool disposing)
    {
      if (disposing && (components != null))
      {
        components.Dispose();
      }
      base.Dispose(disposing);
    }

    #region Windows Form Designer generated code

    /// <summary>
    /// Required method for Designer support - do not modify
    /// the contents of this method with the code editor.
    /// </summary>
    private void InitializeComponent()
    {
      this.capturePanel = new System.Windows.Forms.Panel();
      this.buttonPanel = new System.Windows.Forms.Panel();
      this.acceptButton = new System.Windows.Forms.Button();
      this.panel6 = new System.Windows.Forms.Panel();
      this.cancelButton = new System.Windows.Forms.Button();
      this.panel7 = new System.Windows.Forms.Panel();
      this.buttonPanel.SuspendLayout();
      this.SuspendLayout();
      // 
      // capturePanel
      // 
      this.capturePanel.Dock = System.Windows.Forms.DockStyle.Fill;
      this.capturePanel.Location = new System.Drawing.Point(0, 0);
      this.capturePanel.Name = "capturePanel";
      this.capturePanel.Size = new System.Drawing.Size(342, 72);
      this.capturePanel.TabIndex = 2;
      // 
      // buttonPanel
      // 
      this.buttonPanel.Controls.Add(this.acceptButton);
      this.buttonPanel.Controls.Add(this.panel6);
      this.buttonPanel.Controls.Add(this.cancelButton);
      this.buttonPanel.Dock = System.Windows.Forms.DockStyle.Bottom;
      this.buttonPanel.Location = new System.Drawing.Point(0, 79);
      this.buttonPanel.Name = "buttonPanel";
      this.buttonPanel.Size = new System.Drawing.Size(342, 31);
      this.buttonPanel.TabIndex = 3;
      // 
      // acceptButton
      // 
      this.acceptButton.DialogResult = System.Windows.Forms.DialogResult.OK;
      this.acceptButton.Dock = System.Windows.Forms.DockStyle.Right;
      this.acceptButton.FlatAppearance.BorderColor = System.Drawing.Color.DarkGreen;
      this.acceptButton.FlatAppearance.MouseDownBackColor = System.Drawing.Color.LimeGreen;
      this.acceptButton.FlatAppearance.MouseOverBackColor = System.Drawing.Color.PaleGreen;
      this.acceptButton.Location = new System.Drawing.Point(187, 0);
      this.acceptButton.Name = "acceptButton";
      this.acceptButton.Size = new System.Drawing.Size(75, 31);
      this.acceptButton.TabIndex = 2;
      this.acceptButton.Text = "Accept";
      this.acceptButton.UseVisualStyleBackColor = false;
      this.acceptButton.Click += new System.EventHandler(this.OkButtonClick);
      // 
      // panel6
      // 
      this.panel6.Dock = System.Windows.Forms.DockStyle.Right;
      this.panel6.Location = new System.Drawing.Point(262, 0);
      this.panel6.Name = "panel6";
      this.panel6.Size = new System.Drawing.Size(5, 31);
      this.panel6.TabIndex = 24;
      // 
      // cancelButton
      // 
      this.cancelButton.BackColor = System.Drawing.SystemColors.Control;
      this.cancelButton.DialogResult = System.Windows.Forms.DialogResult.Cancel;
      this.cancelButton.Dock = System.Windows.Forms.DockStyle.Right;
      this.cancelButton.FlatAppearance.BorderColor = System.Drawing.Color.DarkRed;
      this.cancelButton.FlatAppearance.MouseDownBackColor = System.Drawing.Color.IndianRed;
      this.cancelButton.FlatAppearance.MouseOverBackColor = System.Drawing.Color.Salmon;
      this.cancelButton.Location = new System.Drawing.Point(267, 0);
      this.cancelButton.Name = "cancelButton";
      this.cancelButton.Size = new System.Drawing.Size(75, 31);
      this.cancelButton.TabIndex = 3;
      this.cancelButton.Text = "Cancel";
      this.cancelButton.UseVisualStyleBackColor = false;
      // 
      // panel7
      // 
      this.panel7.Dock = System.Windows.Forms.DockStyle.Bottom;
      this.panel7.Location = new System.Drawing.Point(0, 72);
      this.panel7.Name = "panel7";
      this.panel7.Size = new System.Drawing.Size(342, 7);
      this.panel7.TabIndex = 6;
      // 
      // ScaffoldForm
      // 
      this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
      this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
      this.ClientSize = new System.Drawing.Size(342, 110);
      this.ControlBox = false;
      this.Controls.Add(this.capturePanel);
      this.Controls.Add(this.panel7);
      this.Controls.Add(this.buttonPanel);
      this.MaximumSize = new System.Drawing.Size(1024, 768);
      this.MinimumSize = new System.Drawing.Size(350, 110);
      this.Name = "ScaffoldForm";
      this.ShowIcon = false;
      this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
      this.Text = "Add Staff";
      this.buttonPanel.ResumeLayout(false);
      this.ResumeLayout(false);

    }

    #endregion

    private System.Windows.Forms.Panel capturePanel;
    private System.Windows.Forms.Panel buttonPanel;
    private System.Windows.Forms.Button cancelButton;
    private System.Windows.Forms.Button acceptButton;
    private System.Windows.Forms.Panel panel6;
    private System.Windows.Forms.Panel panel7;

  }
}