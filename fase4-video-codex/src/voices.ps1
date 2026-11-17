$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Speech
$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer
try {
  $voices = @($synth.GetInstalledVoices() | Where-Object { $_.Enabled })
  if ($voices.Count -eq 0) { Write-Output 'Nenhuma voz SAPI encontrada.'; exit 1 }
  $voices | ForEach-Object { Write-Output ("{0} | {1}" -f $_.VoiceInfo.Culture.Name, $_.VoiceInfo.Name) }
  if (-not ($voices | Where-Object { $_.VoiceInfo.Culture.Name -like 'pt-*' })) {
    Write-Warning 'Sem voz em portugues no SAPI. Instale voz pt-BR no Windows ou use VOICE_PROVIDER=openai.'
  }
} finally { $synth.Dispose() }
