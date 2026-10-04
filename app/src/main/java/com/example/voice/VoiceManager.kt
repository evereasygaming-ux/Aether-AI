package com.example.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

enum class OrbExpression {
    NEUTRAL,
    LISTENING,
    THINKING,
    SPEAKING,
    HAPPY,
    ALERT
}

class VoiceManager(private val context: Context) : TextToSpeech.OnInitListener {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _audioWave = MutableStateFlow(0.15f)
    val audioWave: StateFlow<Float> = _audioWave.asStateFlow()

    private val _expression = MutableStateFlow(OrbExpression.NEUTRAL)
    val expression: StateFlow<OrbExpression> = _expression.asStateFlow()

    private val _lastSpokenText = MutableStateFlow("")
    val lastSpokenText: StateFlow<String> = _lastSpokenText.asStateFlow()

    private var waveAnimationJob: Job? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("VoiceManager", "Error initializing TTS: ${e.message}")
        }
        startIdleWaveform()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true
                    engine.setPitch(1.18f) // Futuristic sci-fi crisp pitch
                    engine.setSpeechRate(1.08f)
                    setupUtteranceListener()
                }
            }
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
                _expression.value = OrbExpression.SPEAKING
                startSpeakingWaveform()
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                _expression.value = OrbExpression.NEUTRAL
                startIdleWaveform()
            }

            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                _expression.value = OrbExpression.NEUTRAL
                startIdleWaveform()
            }
        })
    }

    fun speak(text: String, expressionOverride: OrbExpression? = null) {
        _lastSpokenText.value = text
        _expression.value = expressionOverride ?: OrbExpression.SPEAKING

        if (isTtsReady && tts != null) {
            val params = android.os.Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "AETHER_VOICE_${System.currentTimeMillis()}")
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "AETHER_VOICE_${System.currentTimeMillis()}")
        } else {
            // Emulated TTS fallback with synchronized voice animation
            scope.launch {
                _isSpeaking.value = true
                startSpeakingWaveform()
                val readingDurationMs = (text.length * 60L).coerceIn(1200L, 5000L)
                delay(readingDurationMs)
                _isSpeaking.value = false
                _expression.value = OrbExpression.NEUTRAL
                startIdleWaveform()
            }
        }
    }

    fun startListeningSimulation(onRecognized: (String) -> Unit) {
        _isListening.value = true
        _expression.value = OrbExpression.LISTENING
        waveAnimationJob?.cancel()
        waveAnimationJob = scope.launch {
            // Simulate audio listening fluctuation
            var ticks = 0
            while (isActive && _isListening.value) {
                _audioWave.value = 0.4f + Random.nextFloat() * 0.55f
                delay(80)
                ticks++
                if (ticks > 35) { // Stop after ~2.8s
                    break
                }
            }
            _isListening.value = false
            _expression.value = OrbExpression.THINKING
            startThinkingWaveform()
            delay(600)
            onRecognized("Activate quantum lockdown and test termux server")
        }
    }

    fun stopListening() {
        _isListening.value = false
        _expression.value = OrbExpression.NEUTRAL
        startIdleWaveform()
    }

    fun setExpression(exp: OrbExpression) {
        _expression.value = exp
        when (exp) {
            OrbExpression.THINKING -> startThinkingWaveform()
            OrbExpression.SPEAKING -> startSpeakingWaveform()
            OrbExpression.LISTENING -> {
                waveAnimationJob?.cancel()
                waveAnimationJob = scope.launch {
                    while (isActive) {
                        _audioWave.value = 0.5f + Random.nextFloat() * 0.45f
                        delay(90)
                    }
                }
            }
            OrbExpression.HAPPY -> {
                waveAnimationJob?.cancel()
                waveAnimationJob = scope.launch {
                    repeat(10) {
                        _audioWave.value = 0.8f
                        delay(100)
                        _audioWave.value = 0.3f
                        delay(100)
                    }
                    _expression.value = OrbExpression.NEUTRAL
                    startIdleWaveform()
                }
            }
            OrbExpression.ALERT -> {
                _audioWave.value = 0.95f
            }
            OrbExpression.NEUTRAL -> startIdleWaveform()
        }
    }

    private fun startIdleWaveform() {
        waveAnimationJob?.cancel()
        waveAnimationJob = scope.launch {
            var phase = 0.0
            while (isActive) {
                phase += 0.1
                _audioWave.value = (0.15f + (Math.sin(phase) * 0.08f).toFloat()).coerceIn(0.05f, 0.4f)
                delay(60)
            }
        }
    }

    private fun startSpeakingWaveform() {
        waveAnimationJob?.cancel()
        waveAnimationJob = scope.launch {
            while (isActive && (_isSpeaking.value || _expression.value == OrbExpression.SPEAKING)) {
                _audioWave.value = 0.35f + Random.nextFloat() * 0.65f
                delay(70)
            }
        }
    }

    private fun startThinkingWaveform() {
        waveAnimationJob?.cancel()
        waveAnimationJob = scope.launch {
            var step = 0
            while (isActive && _expression.value == OrbExpression.THINKING) {
                step = (step + 1) % 6
                _audioWave.value = 0.25f + (step / 6f) * 0.4f
                delay(120)
            }
        }
    }

    fun release() {
        waveAnimationJob?.cancel()
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
