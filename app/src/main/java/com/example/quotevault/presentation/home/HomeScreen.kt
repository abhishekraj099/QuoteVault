package com.example.quotevault.presentation.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.quotevault.core.navigation.Screen
import com.example.quotevault.core.util.Constants
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.presentation.collections.components.AddToCollectionDialog
import com.example.quotevault.presentation.collections.list.CollectionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    collectionsViewModel: CollectionsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val collectionsState by collectionsViewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Add to Collection Dialog state
    var showAddToCollectionDialog by remember { mutableStateOf(false) }
    var selectedQuoteForCollection by remember { mutableStateOf<Quote?>(null) }

    // Show AddToCollectionDialog
    if (showAddToCollectionDialog && selectedQuoteForCollection != null) {
        AddToCollectionDialog(
            collections = collectionsState.collections,
            isLoading = collectionsState.isLoading,
            onDismiss = {
                showAddToCollectionDialog = false
                selectedQuoteForCollection = null
            },
            onCollectionSelected = { collectionId ->
                selectedQuoteForCollection?.let { quote ->
                    collectionsViewModel.onEvent(
                        com.example.quotevault.presentation.collections.list.CollectionsEvent.AddQuoteToCollection(
                            collectionId = collectionId,
                            quoteId = quote.id
                        )
                    )
                }
                showAddToCollectionDialog = false
                selectedQuoteForCollection = null
            },
            onCreateNew = { name ->
                selectedQuoteForCollection?.let { quote ->
                    // Use the new event that creates collection AND adds quote
                    collectionsViewModel.onEvent(
                        com.example.quotevault.presentation.collections.list.CollectionsEvent.CreateCollectionAndAddQuote(
                            name = name,
                            quoteId = quote.id
                        )
                    )
                }
                showAddToCollectionDialog = false
                selectedQuoteForCollection = null
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF5F3FF),
                            Color(0xFFEDE9FE),
                            Color(0xFFE9D5FF)
                        )
                    )
                )
        ) {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = {
                    viewModel.onEvent(HomeEvent.Refresh)
                },
                modifier = Modifier.padding(padding)
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Custom Header
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = "Aura ",
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B),
                                                letterSpacing = (-0.5).sp
                                            )
                                            Text(
                                                text = "Wisdom",
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF8B5CF6),
                                                letterSpacing = (-0.5).sp
                                            )
                                        }
                                        Text(
                                            text = "PERSONAL SANCTUARY",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF94A3B8),
                                            letterSpacing = 1.5.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFF8B5CF6))
                                            .clickable { navController.navigate(Screen.Profile.route) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Profile",
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Search Bar to Navigate to Explore
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.6f))
                                .clickable {
                                    navController.navigate(Screen.Search.route)
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Explore quotes, authors...",
                                    fontSize = 15.sp,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }

                    // Quote of the Day
                    item {
                        state.quoteOfTheDay?.let { quote ->
                            EnhancedQuoteOfTheDayCard(
                                quote = quote,
                                onFavoriteClick = {
                                    viewModel.onEvent(HomeEvent.ToggleFavorite(quote.id))
                                },
                                onShareClick = {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "\"${quote.text}\" - ${quote.author}")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Quote"))
                                },
                                onAddToCollectionClick = {
                                    selectedQuoteForCollection = quote
                                    showAddToCollectionDialog = true
                                }
                            )
                        }
                    }

                    // Section Header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 24.dp, bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LATEST STREAM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 1.5.sp
                            )
                        }
                    }

                    // Category Tabs
                    item {
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                            CategoryTabs(
                                categories = Constants.CATEGORIES,
                                selectedCategory = state.selectedCategory,
                                onCategorySelected = {
                                    viewModel.onEvent(HomeEvent.CategorySelected(it))
                                }
                            )
                        }
                    }

                    // Quote Cards with Smooth Stacking Animation
                    items(state.quotes.size) { index ->
                        val quote = state.quotes[index]

                        // Get layout info for scroll-based animation
                        val layoutInfo = listState.layoutInfo
                        val visibleItem = layoutInfo.visibleItemsInfo.find {
                            it.index == index + 4
                        }

                        // Calculate card transformations
                        val itemOffset = visibleItem?.offset ?: Int.MAX_VALUE
                        val viewportStart = layoutInfo.viewportStartOffset
                        val viewportHeight = layoutInfo.viewportEndOffset - viewportStart

                        // Distance from top of viewport
                        val distanceFromTop = (itemOffset - viewportStart).toFloat()

                        // Smooth scale animation (cards scale down as they approach top)
                        val scale = when {
                            distanceFromTop < 0 -> 0.85f // Already passed
                            distanceFromTop < 150 -> {
                                // Smooth transition from 1.0 to 0.9
                                0.9f + (distanceFromTop / 150f * 0.1f)
                            }
                            else -> 1f
                        }

                        // Smooth Y translation for stacking effect
                        val translationY = when {
                            distanceFromTop < 0 -> -80f
                            distanceFromTop < 120 -> {
                                // Smooth slide up effect
                                -(120f - distanceFromTop) * 0.8f
                            }
                            else -> 0f
                        }

                        // Smooth opacity fade
                        val alpha = when {
                            distanceFromTop < 0 -> 0f
                            distanceFromTop < 100 -> distanceFromTop / 100f
                            else -> 1f
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 12.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    this.translationY = translationY
                                    this.alpha = alpha
                                }
                                .zIndex((1000 - index).toFloat())
                        ) {
                            EnhancedQuoteCard(
                                quote = quote,
                                onFavoriteClick = {
                                    viewModel.onEvent(HomeEvent.ToggleFavorite(quote.id))
                                },
                                onShareClick = {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "\"${quote.text}\" - ${quote.author}")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Quote"))
                                },
                                onAddToCollectionClick = {
                                    selectedQuoteForCollection = quote
                                    showAddToCollectionDialog = true
                                }
                            )
                        }
                    }

                    // Load More Trigger
                    if (state.hasMorePages && !state.isLoading) {
                        item {
                            LaunchedEffect(Unit) {
                                viewModel.onEvent(HomeEvent.LoadMore)
                            }
                        }
                    }

                    // Loading Indicator
                    if (state.isLoading && state.quotes.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF8B5CF6),
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                // Initial Loading State
                if (state.isLoading && state.quotes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF8B5CF6),
                            strokeWidth = 4.dp
                        )
                    }
                }

                // Error State
                if (state.error != null && state.quotes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Failed to load quotes",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Please check your internet connection",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.onEvent(HomeEvent.Refresh) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8B5CF6)
                                )
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedQuoteOfTheDayCard(
    quote: Quote,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onAddToCollectionClick: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    // Continuous bouncy animation - comes up and goes down repeatedly
    val infiniteTransition = rememberInfiniteTransition(label = "bounceTransition")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceOffset"
    )

    // Smooth scale animation on hover/press
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.96f
            isHovered -> 1.03f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    // Smooth elevation animation
    val elevation by animateDpAsState(
        targetValue = if (isHovered) 12.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "elevation"
    )

    // Smooth Y translation for "lift up" effect
    val translationY by animateFloatAsState(
        targetValue = if (isHovered) -8f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "translationY"
    )

    // Pulsing glow animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.translationY = translationY + bounceOffset
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            isHovered = true
                            tryAwaitRelease()
                            isPressed = false
                            kotlinx.coroutines.delay(200)
                            isHovered = false
                        }
                    )
                },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF8B5CF6)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column {
                    // Header Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "THE DAILY AURA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f),
                                letterSpacing = 1.sp
                            )
                        }

                        // Live Wisdom Badge
                        Surface(
                            color = Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                val pulseScale by infiniteTransition.animateFloat(
                                    initialValue = 0.8f,
                                    targetValue = 1.2f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1000),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "pulse"
                                )

                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .graphicsLayer {
                                            scaleX = pulseScale
                                            scaleY = pulseScale
                                        }
                                        .background(Color(0xFF4ADE80), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE WISDOM",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quote Text
                    Text(
                        text = quote.text,
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Author
                    Text(
                        text = "— ${quote.author.uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var saveButtonPressed by remember { mutableStateOf(false) }
                        val saveScale by animateFloatAsState(
                            targetValue = if (saveButtonPressed) 0.92f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "saveScale"
                        )

                        Surface(
                            onClick = onFavoriteClick,
                            shape = RoundedCornerShape(16.dp),
                            color = if (quote.isFavorite)
                                Color(0xFFEF4444)
                            else
                                Color.White.copy(alpha = 0.25f),
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = saveScale
                                    scaleY = saveScale
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            saveButtonPressed = true
                                            tryAwaitRelease()
                                            saveButtonPressed = false
                                        }
                                    )
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    imageVector = if (quote.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Save",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (quote.isFavorite) "SAVED" else "SAVE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Action buttons row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Bookmark button
                            var bookmarkButtonPressed by remember { mutableStateOf(false) }
                            val bookmarkScale by animateFloatAsState(
                                targetValue = if (bookmarkButtonPressed) 0.85f else 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "bookmarkScale"
                            )

                            IconButton(
                                onClick = onAddToCollectionClick,
                                modifier = Modifier
                                    .size(44.dp)
                                    .graphicsLayer {
                                        scaleX = bookmarkScale
                                        scaleY = bookmarkScale
                                    }
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                bookmarkButtonPressed = true
                                                tryAwaitRelease()
                                                bookmarkButtonPressed = false
                                            }
                                        )
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.BookmarkAdd,
                                    contentDescription = "Add to Collection",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Share button
                            var shareButtonPressed by remember { mutableStateOf(false) }
                            val shareScale by animateFloatAsState(
                                targetValue = if (shareButtonPressed) 0.85f else 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "shareScale"
                            )

                            IconButton(
                                onClick = onShareClick,
                                modifier = Modifier
                                    .size(44.dp)
                                    .graphicsLayer {
                                        scaleX = shareScale
                                        scaleY = shareScale
                                    }
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                shareButtonPressed = true
                                                tryAwaitRelease()
                                                shareButtonPressed = false
                                            }
                                        )
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = "Share",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedQuoteCard(
    quote: Quote,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onAddToCollectionClick: () -> Unit = {}
) {
    var isCardPressed by remember { mutableStateOf(false) }
    var isCardHovered by remember { mutableStateOf(false) }

    // Continuous gentle bouncy animation - comes up and goes down repeatedly
    val infiniteTransition = rememberInfiniteTransition(label = "cardBounceTransition")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceOffset"
    )

    // Smooth scale animation on hover/press
    val cardScale by animateFloatAsState(
        targetValue = when {
            isCardPressed -> 0.97f
            isCardHovered -> 1.02f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )

    // Smooth elevation animation
    val cardElevation by animateDpAsState(
        targetValue = if (isCardHovered) 8.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardElevation"
    )

    // Smooth Y translation for "lift up" effect
    val cardTranslationY by animateFloatAsState(
        targetValue = if (isCardHovered) -6f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardTranslationY"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
                translationY = cardTranslationY + bounceOffset
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isCardPressed = true
                        isCardHovered = true
                        tryAwaitRelease()
                        isCardPressed = false
                        kotlinx.coroutines.delay(200)
                        isCardHovered = false
                    }
                )
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAF5FF)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardElevation
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column {
                // Quote Text
                Text(
                    text = quote.text,
                    fontSize = 17.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Author
                Text(
                    text = "— ${quote.author.uppercase()}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B5CF6),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var saveButtonPressed by remember { mutableStateOf(false) }
                    val saveScale by animateFloatAsState(
                        targetValue = if (saveButtonPressed) 0.92f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "saveScale"
                    )

                    Surface(
                        onClick = onFavoriteClick,
                        shape = RoundedCornerShape(14.dp),
                        color = if (quote.isFavorite)
                            Color(0xFFEF4444)
                        else
                            Color(0xFF8B5CF6),
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = saveScale
                                scaleY = saveScale
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        saveButtonPressed = true
                                        tryAwaitRelease()
                                        saveButtonPressed = false
                                    }
                                )
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp)
                        ) {
                            Icon(
                                imageVector = if (quote.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Save",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (quote.isFavorite) "SAVED" else "SAVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Action buttons row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Bookmark button (Add to Collection)
                        var bookmarkButtonPressed by remember { mutableStateOf(false) }
                        val bookmarkScale by animateFloatAsState(
                            targetValue = if (bookmarkButtonPressed) 0.85f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "bookmarkScale"
                        )

                        IconButton(
                            onClick = onAddToCollectionClick,
                            modifier = Modifier
                                .size(42.dp)
                                .graphicsLayer {
                                    scaleX = bookmarkScale
                                    scaleY = bookmarkScale
                                }
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            bookmarkButtonPressed = true
                                            tryAwaitRelease()
                                            bookmarkButtonPressed = false
                                        }
                                    )
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkAdd,
                                contentDescription = "Add to Collection",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Share button
                        var shareButtonPressed by remember { mutableStateOf(false) }
                        val shareScale by animateFloatAsState(
                            targetValue = if (shareButtonPressed) 0.85f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "shareScale"
                        )

                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier
                                .size(42.dp)
                                .graphicsLayer {
                                    scaleX = shareScale
                                    scaleY = shareScale
                                }
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            shareButtonPressed = true
                                            tryAwaitRelease()
                                            shareButtonPressed = false
                                        }
                                    )
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Share",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTabs(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Surface(
                onClick = { onCategorySelected(category) },
                shape = RoundedCornerShape(18.dp),
                color = if (isSelected)
                    Color(0xFF8B5CF6)
                else
                    Color.White.copy(alpha = 0.7f)
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected)
                        Color.White
                    else
                        Color(0xFF64748B)
                )
            }
        }
    }
}