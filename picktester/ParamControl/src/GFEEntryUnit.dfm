object EntryForm: TEntryForm
  Left = 356
  Top = 387
  Width = 340
  Height = 97
  Caption = 'Parameter Control Maintenance Entry Form'
  Color = clBtnFace
  Constraints.MinWidth = 340
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  Position = poDesktopCenter
  OnActivate = FormActivate
  OnCreate = FormCreate
  DesignSize = (
    332
    70)
  PixelsPerInch = 96
  TextHeight = 13
  object OkButton: TButton
    Left = 88
    Top = 43
    Width = 75
    Height = 25
    Anchors = [akLeft, akBottom]
    Caption = '&Ok'
    Default = True
    TabOrder = 0
    OnClick = OkButtonClick
  end
  object CancelButton: TButton
    Left = 172
    Top = 43
    Width = 75
    Height = 25
    Anchors = [akLeft, akBottom]
    Caption = '&Cancel'
    ModalResult = 2
    TabOrder = 1
  end
end
