package com.example.arpapractice.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.method.DigitsKeyListener
import android.util.DisplayMetrics
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.arpapractice.R
import java.util.*
import java.util.regex.Matcher
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.text.InputType
import android.util.Patterns
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.getColorOrThrow
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar.make
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import kotlin.collections.ArrayList


/**
 * Extension [AppCompatActivity]: Show Alert Dialog
 * @param title Title of the Dialog
 * @param message Message of the Dialog
 */
fun AppCompatActivity.showAlert(title: CharSequence? = null, message: CharSequence? = null, onPositiveButtonClick: () -> Unit) {
    val builder = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK") { dialog, _ ->
            onPositiveButtonClick()
            dialog.dismiss()
        }
        .setNegativeButton("Cancel"){dialog, _ ->
            dialog.dismiss()
        }

    val dialog: AlertDialog = builder.create()
    dialog.show()
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.black))
    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.black))
}


fun EditText.onlyNumbers() {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or
            InputType.TYPE_NUMBER_FLAG_SIGNED
    keyListener = DigitsKeyListener.getInstance("0123456789")
}

// To use context.toast("Hello world!")
fun Context.toastLong(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

@Suppress("DEPRECATION")
fun <T> Context.isServiceRunning(service: Class<T>) =
    (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }


fun MenuItem.setTitleColor(color: Int) {
    val hexColor = Integer.toHexString(color).toUpperCase(Locale.getDefault()).substring(2)
    val html = "<font color='#$hexColor'>$title</font>"
    this.title = html.parseAsHtml()
}

@Suppress("DEPRECATION")
fun String.parseAsHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}

fun EditText.showKeyboard() {
    post {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun EditText.placeCursorToEnd() {
    this.setSelection(this.text.length)
}

fun String.capitalize(): String? {
    val capBuffer = StringBuffer()
    val capMatcher: Matcher =
        Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(this)
    while (capMatcher.find()) {
        capMatcher.appendReplacement(
            capBuffer,
            capMatcher.group(1)?.toUpperCase(Locale.getDefault()) + capMatcher.group(2)?.toLowerCase(Locale.getDefault())
        )
    }
    return capMatcher.appendTail(capBuffer).toString()
}


fun updateCountDownText(timerTimeInMillis: Long): String {
    val hoursFromMillis = (timerTimeInMillis / 1000) / 3600
    val minutesFromMillis = ((timerTimeInMillis / 1000) % 3600) / 60
    val secondsFromMillis = (timerTimeInMillis / 1000) % 60

    return if (hoursFromMillis > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hoursFromMillis, minutesFromMillis, secondsFromMillis)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutesFromMillis, secondsFromMillis)
    }
}

fun viewChangeBackgroundImageView(context: Context, view: ImageView, drawable: Int) {
    view.setImageDrawable(ContextCompat.getDrawable(context, drawable))
}

fun viewChangeBackgroundResource(view: View, backgroundResId: Int) {
    view.setBackgroundResource(backgroundResId)
}


fun getDisplaySize(activity: Activity): String? {
    var x = 0.0
    var y = 0.0
    val mWidthPixels: Int
    val mHeightPixels: Int
    try {
        val windowManager = activity.windowManager
        val display = windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)
        val realSize = Point()
        Display::class.java.getMethod("getRealSize", Point::class.java).invoke(display, realSize)
        mWidthPixels = realSize.x
        mHeightPixels = realSize.y
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        x = Math.pow(mWidthPixels / dm.xdpi.toDouble(), 2.0)
        y = Math.pow(mHeightPixels / dm.ydpi.toDouble(), 2.0)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    // return java.lang.String.format(Locale.US, "%.2f", Math.sqrt(x + y))
    return java.lang.String.format(Locale.US, "%.2f", Math.sqrt(x + y))
}


/**
 * Extension [Context]: Check if network is connected or not
 */
fun Context.isConnectedToNetwork(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting ?: false
}

/**
 * Extension [Toast]: Show Short Length Toast
 * @param message String to of Toast
 */
fun Context.toast(message: CharSequence?) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

const val EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$"

// used for validate if the current String is an email
fun String.isValidEmail(): Boolean {
    val pattern = Pattern.compile(EMAIL_PATTERN)
    return pattern.matcher(this).matches()
}

/**
 * Extension [View]: Visible View
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * Extension [View]: Invisible View
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Extension [View]: Gone View
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * Extension [Context]: Dismiss Keyboard
 * @param view The View on Which it class
 */
fun Context.dismissKeyboard(view: View?) {
    view?.let {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

/**
 * Extension [View]: Close Keyboard on View Touch
 * @param conText Context from which fun calls
 * @param rootView the parent View
 */
fun View.closeKeyboard(conText: Context, rootView: View) {
    setOnTouchListener { v, event ->
        if ((event.action == MotionEvent.ACTION_UP)) {
            v.requestFocus()
            conText.dismissKeyboard(rootView)
            true
        } else
            false
    }
}

/**
 * Extension [EditText]: Toggle Password Show/Hide
 */
fun EditText.togglePassWord(): Boolean {
    this.tag = !((this.tag ?: false) as Boolean)
    this.inputType = if (this.tag as Boolean)
        InputType.TYPE_TEXT_VARIATION_PASSWORD
    else
        (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)

    this.setSelection(length())

    return this.tag as Boolean
}

/**
 * Extension [Context]: Change Image of ImageView
 * @param view ImageView
 */
fun Context.changeImage(view: ImageView, @DrawableRes drawableResId: Int) {
    view.setImageDrawable(ContextCompat.getDrawable(this, drawableResId))
}

/**
 * Extension [EditText]: Change Drawable End of EditText
 */
fun EditText.drawableEnd(@DrawableRes id: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, id, 0)
}

/**
 * Extension [Button]: Button disabling modifiers
 */
fun Button.disableButton() {
    isEnabled = false
    alpha = 0.3f
}

/**
 * Extension [Button]: Button enabling modifiers
 */
fun Button.enableButton() {
    isEnabled = true
    alpha = 1f
}

/**
 * Extension [EditText]: EditText onRight Drawable Click
 */
@SuppressLint("ClickableViewAccessibility")
fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
    setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is EditText) {
            if (event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}

/**
 * Extension [Context]: navigate To
 * @param it Giving Class
 */
fun <T> Context.navigateTo(it: Class<T>) {
    val intent = Intent(this, it)
    startActivity(intent)
}

/**
 * Extension [Context]: navigate To with giving Flags
 * @param it Giving Class
 */
fun <T> Context.navigateToWithClearBackStack(it: Class<T>) {
    val intent = Intent(this, it).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}

/**
 * Extension [String]: Check if the giving String is URL or not
 */
fun String.isValidUrl(): Boolean = !(Patterns.WEB_URL.matcher(this).matches())

/**
 * Extension [EditText]: On Keyboard Done Button Click
 */
fun EditText.onIMEDoneClick(callback: () -> Unit) {
    // These lines optional if you don't want to set in Xml
    imeOptions = EditorInfo.IME_ACTION_DONE
    maxLines = 1
    setOnEditorActionListener { v, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            this.context.dismissKeyboard(v)
        }
        false
    }
}

/**
 * Extension [MaterialCardView]: Change CardView Bounds [Margin, Elevation & Radius] on When It's child EditText Gains Focus
 */
fun MaterialCardView.changeBoundsOfEditTextOnFocusGain() {
    val layoutParams: ViewGroup.MarginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(5, 5, 5, 5)
    elevation = 10.0f
    radius = 10.0f
    requestLayout()
}

/**
 * Extension [MaterialCardView]: Change CardView Bounds [Margin, Elevation & Radius] on When It's child EditText Lost Focus
 */
fun MaterialCardView.changeBoundsOfEditTextOnFocusLost() {
    val layoutParams: ViewGroup.MarginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(0, 0, 0, 0)
    elevation = 0.0f
    radius = 0.0f
    requestLayout()
}


/**
 * Extension [EditText]: EditText as TextArea to Allow Scroll in Parent
 * @param scrollView Parent View
 */
fun EditText.allowTextAreaScroll(scrollView: ScrollView) {
    setOnTouchListener { v, event ->
        if (hasFocus()) {
            scrollView.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_SCROLL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener true
                }
            }
        }
        false
    }
}

/**
 * Extension [SharedPreferences]: Put all Most all Primitive Data type in pref
 * @param key Key of th the Value
 * @param value the value which will saved in this pref
 */
fun <T> SharedPreferences.putAnyThing(key: String, value: T) {
    with(edit()) {
        when (value) {
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is String -> putString(key, value)
            else -> throw IllegalArgumentException()
        }.apply()
    }
}

/**
 * Extension [SharedPreferences]: Get all Most all Primitive Data type from pref
 * @param key Key of th the Value
 * @param defaultValue the value which will replace to the original value if original Value not found
 */
fun <T> SharedPreferences.getAnyThing(key: String, defaultValue: T): T {
    with(this) {
        val result: Any = when (defaultValue) {
            is String -> getString(key, defaultValue) as String
            is Boolean -> getBoolean(key, defaultValue)
            is Int -> getInt(key, defaultValue)
            is Long -> getLong(key, defaultValue)
            is Float -> getFloat(key, defaultValue)
            else -> throw IllegalArgumentException()
        }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }
}

/**
 * Extension [SharedPreferences]: Put Object in the pref
 * @param key Key of th the Value
 * @param mObject The Object which will save in the pref
 */
fun <T> SharedPreferences.putObject(key: String, mObject: T) {
    val jsonObject = Gson().toJson(mObject)
    edit().putString(key, jsonObject).apply()
}

/**
 * Extension [SharedPreferences]: Get an Object from the pref
 * @param key Key of th the Value
 * @param mClass Type of the Object
 * @param T The Object
 */
fun <T> SharedPreferences.getObject(key: String, mClass: Class<T>): T? {
    val value = getString(key, null)
    return if (value != null) {
        Gson().fromJson(value, mClass)
    } else null
}

/**
 * Extension [Context]: Get Primary Color and convert it into INT
 * Used For: textField.TextInputLayout
 * @return the Color in Int
 */
fun Context.fetchPrimaryColor(): Int {
    val array = obtainStyledAttributes(intArrayOf(android.R.attr.colorPrimary))
    val color = array.getColorOrThrow(0)
    array.recycle()
    return color
}

/**
 * Extension [AppCompatActivity]: Show Alert Dialog
 * @param title Title of the Dialog
 * @param message Message of the Dialog
 */
fun AppCompatActivity.showAlert(title: CharSequence? = null, message: CharSequence? = null) {
    val builder = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }

    val dialog: AlertDialog = builder.create()
    dialog.show()
}

/**
 * Extension [Context]: Show Not Network Error in SnackBar
 */
fun Context.noNetworkIsFound(rootView: View) {
    make(
        rootView,
        "No Network found",
        LENGTH_LONG
    ).show()
}

/**
 * Extension [Context]: Show SnackBar Long
 * @param rootView Parent View of the Layout
 * @param message Message of the SnackBar
 */
fun Context.showSnackBarLong(rootView: View, message: CharSequence) {
    make(
        rootView,
        message,
        LENGTH_LONG
    ).show()
}

/**
 * Extension [Context]: Show SnackBar Short
 * @param rootView Parent View of the Layout
 * @param message Message of the SnackBar
 */
fun Context.showSnackBarShort(rootView: View, message: CharSequence) {
    make(
        rootView,
        message,
        LENGTH_SHORT
    ).show()
}

/**
 * Extension [List]: Convert List to ArrayList
 * @return ArrayList
 */
fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}

/**
 * Extension [TextView]: Change TextView Color
 */
fun TextView.changeColor(color: Int) = this.setTextColor(color)

/**
 * Extension [String]: Convert String to Color
 */
fun String.toColor(): Int = Color.parseColor(this)

/**
 * Extension [MaterialButton]: Change Background of Button
 * @param resId Recourse Id of the Background
 */
fun MaterialButton.changeBackground(resId: Int) {
    backgroundTintList = this.context.resources.getColorStateList(resId)
}

/**
 *  Helper Function to convert Comma Separated String into List
 */
fun  String.convertCommaSeparatedStringToList(): List<String>? {
    return try {
        // Check if String has one Value or more
        if (this.contains(","))
        // If it has more value then convert it into List
            this.split(",").map { it.trim() }
        else
        // Else just make a List of One Value
            listOfNotNull(this)
    } catch (e: Exception) {
        // If Error occurs send Null
        null
    }
}

/**
 *  Extension [Spinner]: On item Selected Listener Custom Lambda
 */
inline fun Spinner.onItemSelected(
    crossinline itemSelected: (
        parent: AdapterView<*>,
        view: View,
        position: Int,
        id: Long
    ) -> Unit
) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            itemSelected.invoke(parent, view, position, id)
        }
    }
}

/**
 *  Extension [Uri]: Change Background of Button
 *  @param context Context from which it is called
 */
fun Uri?.openInBrowser(context: Context) {
    this ?: return // Do nothing if uri is null
    val browserIntent = Intent(Intent.ACTION_VIEW, this)
    ContextCompat.startActivity(context, browserIntent, null)
}

/**
 *  Extension [String]: Convert String into URI
 */
fun String?.asUri(): Uri? {
    try {
        return Uri.parse(this)
    } catch (e: Exception) {}
    return null
}

/**
 * Extension [Long]: Return Formatted Date [yyyy-mm-dd] to given Timestamp
 */
fun Long.updateDateInSendFormat():String{
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val netDate = Date(this*1000)
    return sdf.format(netDate)
}

/**
 *  Extension [String]: Convert String into URI
 */
fun String.validateCode(): Boolean {
    val regex = "^([a-z0-9A-Z\\#\\+\\-\\.]+\\,\\s?)*[a-z0-9A-Z\\#\\+\\-\\.]+$".toRegex()
    return this.matches(regex)
}