package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import android.media.MediaPlayer
import com.example.R
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ArrowBack
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import com.example.ui.theme.MyApplicationTheme
import kotlin.math.sin
import kotlin.random.Random
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
  private var mediaPlayer: MediaPlayer? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mediaPlayer = MediaPlayer()
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.light(
            android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
        ),
        navigationBarStyle = SystemBarStyle.light(
            android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
        )
    )
    setContent {
      MyApplicationTheme(dynamicColor = false) {
        mediaPlayer?.let { AppNavigation(it) }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    if (MusicManager.getCurrentResId() != R.raw.dummy_bgm) {
        MusicManager.stopAndRelease()
    }
    mediaPlayer?.let {
      if (!it.isPlaying) {
        it.start()
      }
    }
  }

  override fun onPause() {
    super.onPause()
    mediaPlayer?.let {
      if (it.isPlaying) {
        it.pause()
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    mediaPlayer?.let {
      if (it.isPlaying) {
        it.stop()
      }
      it.release()
    }
    mediaPlayer = null
  }
}

@Composable
fun AppNavigation(mediaPlayer: MediaPlayer) {
  val navController = rememberNavController()
  val context = LocalContext.current
  var isMuted by remember { mutableStateOf(false) }

  val currentBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = currentBackStackEntry?.destination?.route

  DisposableEffect(currentRoute) {
    try {
      mediaPlayer.reset()
      mediaPlayer.setAudioAttributes(
        android.media.AudioAttributes.Builder()
          .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
          .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
          .build()
      )
      
      val audioRes = when (currentRoute) {
        "angel" -> R.raw.bg_audio
        "gallery" -> R.raw.new_song
        else -> null
      }
      
      if (audioRes != null) {
        val uri = android.net.Uri.parse("android.resource://${context.packageName}/$audioRes")
        mediaPlayer.setDataSource(context, uri)
        mediaPlayer.isLooping = true
        mediaPlayer.prepare()
        mediaPlayer.start()
        val volume = if (isMuted) 0f else 1f
        mediaPlayer.setVolume(volume, volume)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    
    onDispose {
      try {
        if (mediaPlayer.isPlaying) {
          mediaPlayer.stop()
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  // MediaPlayer is managed by MainActivity


  LaunchedEffect(isMuted) {
    try {
      val volume = if (isMuted) 0f else 1f
      mediaPlayer.setVolume(volume, volume)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  Box(
    modifier = Modifier.fillMaxSize()
  ) {
    NavHost(navController = navController, startDestination = "home") {
      composable("home") {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          BirthdayScreen(
            modifier = Modifier.padding(innerPadding),
            onNavigate = { 
              MusicManager.stopAndRelease()
              navController.navigate("angel")
            }
          )
        }
      }
      composable("angel") {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          HeavenAngelScreen(
            modifier = Modifier.padding(innerPadding),
            onBack = { navController.popBackStack() }
          )
        }
      }
    }
    
    SparklesOverlay(modifier = Modifier.fillMaxSize())

    FloatingActionButton(
      onClick = { isMuted = !isMuted },
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .padding(bottom = 32.dp),
      containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
      Icon(
        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
        contentDescription = if (isMuted) "Unmute" else "Mute"
      )
    }
  }
}

@Composable
fun BirthdayScreen(modifier: Modifier = Modifier, onNavigate: () -> Unit = {}) {
  var isGreetingLoaded by remember { mutableStateOf(false) }
  val gradient = Brush.verticalGradient(
    colors = listOf(
      MaterialTheme.colorScheme.background,
      MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
      MaterialTheme.colorScheme.background
    )
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(gradient),
    contentAlignment = Alignment.Center
  ) {
    FloatingBalloonsAnimation()
    ConfettiAnimation(isTriggered = isGreetingLoaded)
    
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      AnimatedGreetingText(onLoaded = { isGreetingLoaded = true })
      Spacer(modifier = Modifier.height(24.dp))
      GlowingButton(
        onClick = onNavigate
      ) {
        Text("See Heaven Angel")
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
      }
    }
  }
}

@Composable
fun AnimatedGreetingText(onLoaded: () -> Unit = {}) {
  var startAnimation by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    startAnimation = true
  }

  val scale by animateFloatAsState(
    targetValue = if (startAnimation) 1f else 0.5f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
    label = "scale"
  )

  val alpha by animateFloatAsState(
    targetValue = if (startAnimation) 1f else 0f,
    animationSpec = tween(durationMillis = 1500),
    label = "alpha"
  )

  LaunchedEffect(alpha) {
    if (alpha == 1f) {
      onLoaded()
    }
  }

  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = "HAPPY BIRTHDAY",
      fontSize = 24.sp,
      letterSpacing = 4.sp,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier
        .alpha(alpha)
        .padding(bottom = 8.dp)
    )
    Text(
      text = "Ira",
      fontSize = 80.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Serif,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier
        .scale(scale)
        .alpha(alpha)
    )
  }
}

@Composable
fun HeavenAngelScreen(modifier: Modifier = Modifier, onBack: () -> Unit = {}) {
  val gradient = Brush.verticalGradient(
    colors = listOf(
      MaterialTheme.colorScheme.background,
      Color(0xFFFFE0E9),
      MaterialTheme.colorScheme.background
    )
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(gradient),
    contentAlignment = Alignment.Center
  ) {
    ConfettiAnimation()
    
    IconButton(
      onClick = onBack,
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(16.dp)
        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)
    ) {
      Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = "Back",
        tint = MaterialTheme.colorScheme.primary
      )
    }
    
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      Text(
        text = "My Heaven Angel",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 16.dp)
      )
      
      Box(modifier = Modifier.weight(1f, fill = false)) {
        AsyncImage(
          model = R.drawable.ira_photo,
          contentDescription = "Ira's Photo",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .shadow(16.dp, RoundedCornerShape(24.dp))
        )
      }
      
      Spacer(modifier = Modifier.height(16.dp))
      
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
      ) {
        Text(
          text = "You are the most beautiful part of every day. Thank you for being you! ✨",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(16.dp)
        )
      }
      
      Spacer(modifier = Modifier.height(24.dp))
      
      val context = LocalContext.current
      GlowingButton(
        onClick = {
            try {
                val intent = android.content.Intent(context, GalleryActivity::class.java)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
      ) {
        Text("Open Gallery")
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
      }
    }
  }
}

@Composable
fun GlowingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val composition by com.airbnb.lottie.compose.rememberLottieComposition(
        com.airbnb.lottie.compose.LottieCompositionSpec.Asset("button_glitter_blink.json")
    )

    Box(
        modifier = modifier
    ) {
        val gradientBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF4A0010), Color(0xFF800020)) // Dark maroon to crimson
        )
        Button(
            onClick = onClick,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    alpha = alpha
                )
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color(0xFF800020),
                    ambientColor = Color(0xFF800020)
                )
                .background(gradientBrush, RoundedCornerShape(24.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            androidx.compose.material3.ProvideTextStyle(
                value = androidx.compose.material3.LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Bold
                )
            ) {
                content()
            }
        }
        
        com.airbnb.lottie.compose.LottieAnimation(
            composition = composition,
            iterations = com.airbnb.lottie.compose.LottieConstants.IterateForever,
            modifier = Modifier.matchParentSize()
        )
    }
}

@Composable
fun SparklesOverlay(modifier: Modifier = Modifier) {
  val sparkles = remember {
    List(50) {
      Sparkle(
        x = Random.nextFloat(),
        y = Random.nextFloat(),
        size = Random.nextFloat() * 6f + 2f,
        phase = Random.nextFloat() * 2f * Math.PI.toFloat(),
        speed = Random.nextFloat() * 2f + 1f
      )
    }
  }

  val infiniteTransition = rememberInfiniteTransition()
  val time by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 2f * Math.PI.toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(10000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    )
  )

  Canvas(
    modifier = modifier
      .fillMaxSize()
  ) {
    val width = size.width
    val height = size.height

    sparkles.forEach { sparkle ->
      // Calculate twinkling alpha using a sine wave
      val alpha = ((sin(time * sparkle.speed + sparkle.phase) + 1f) / 2f).coerceIn(0f, 1f)
      
      // Calculate slow vertical drift
      val driftY = (sparkle.y * height - time * sparkle.speed * 20f) % height
      val finalY = if (driftY < 0) driftY + height else driftY

      drawCircle(
        color = Color.White.copy(alpha = alpha * 0.8f),
        radius = sparkle.size * (alpha * 0.5f + 0.5f),
        center = Offset(sparkle.x * width, finalY)
      )
    }
  }
}

data class Sparkle(
  val x: Float,
  val y: Float,
  val size: Float,
  val phase: Float,
  val speed: Float
)

@Composable
fun ConfettiAnimation(isTriggered: Boolean = true) {
  if (!isTriggered) return

  val party = remember {
    Party(
      speed = 0f,
      maxSpeed = 15f,
      damping = 0.9f,
      angle = 90,
      spread = 90,
      colors = listOf(0xFFD4AF37.toInt(), 0xFFFFD700.toInt(), 0xFFDAA520.toInt(), 0xFFB8860B.toInt()),
      emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(80),
      position = Position.Relative(0.0, -0.1).between(Position.Relative(1.0, -0.1))
    )
  }

  KonfettiView(
    modifier = Modifier.fillMaxSize(),
    parties = listOf(party)
  )
}



