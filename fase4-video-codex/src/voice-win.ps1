param(
  [Parameter(Mandatory=$true)][string]$Text,
  [Parameter(Mandatory=$true)][string]$OutFile
)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Speech
$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer
try {
  $voices = @($synth.GetInstalledVoices() | Where-Object { $_.Enabled })
  $ptVoice = $voices | Where-Object { $_.VoiceInfo.Culture.Name -eq 'pt-BR' } | Select-Object -First 1
  if (-not $ptVoice) { $ptVoice = $voices | Where-Object { $_.VoiceInfo.Culture.Name -like 'pt-*' } | Select-Object -First 1 }
  if (-not $ptVoice) {
    throw 'Nenhuma voz portuguesa SAPI instalada. Execute npm run voices e instale uma voz pt-BR ou use VOICE_PROVIDER=openai.'
  }
  $synth.SelectVoice($ptVoice.VoiceInfo.Name)
  $synth.Rate = 2
  $synth.Volume = 100
  $synth.SetOutputToWaveFile($OutFile)
  $synth.Speak($Text)
  $synth.SetOutputToDefaultAudioDevice()
} finally { $synth.Dispose() }
