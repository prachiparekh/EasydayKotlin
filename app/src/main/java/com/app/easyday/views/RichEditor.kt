package com.app.easyday.views


import android.R
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.webkit.WebChromeClient
import android.webkit.WebView
import jp.wasabeef.richeditor.RichEditor
import jp.wasabeef.richeditor.Utils
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*


class RichEditor(context: Context, attrs: AttributeSet) : WebView(context) {
    enum class Type {
        BOLD, ITALIC, SUBSCRIPT, SUPERSCRIPT, STRIKETHROUGH, UNDERLINE, H1, H2, H3, H4, H5, H6, ORDEREDLIST, UNORDEREDLIST, JUSTIFYCENTER, JUSTIFYFULL, JUSTIFYLEFT, JUSTIFYRIGHT
    }

    interface OnTextChangeListener {
        fun onTextChange(text: String?)
    }

    interface OnDecorationStateListener {
        fun onStateChangeListener(text: String?, types: MutableList<Type>)
    }

    interface AfterInitialLoadListener {
        fun onAfterInitialLoad(isReady: Boolean)
    }

    var isReady = false
    var SETUP_HTML =
        "file:///android_asset/editor.html"
    var CALLBACK_SCHEME: String? = "re-callback://"
    var STATE_SCHEME: String? = "re-state://"
    private var mContents: String? = null
    private var mTextChangeListener: OnTextChangeListener? = null
    private var mDecorationStateListener: OnDecorationStateListener? = null
    private var mLoadListener: AfterInitialLoadListener? = null


    init {
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        settings.javaScriptEnabled = true
        webChromeClient = WebChromeClient()
//        createWebviewClient()?.let { setWebViewClient(it) }
        loadUrl(SETUP_HTML)
        applyAttributes(context, attrs)
    }


    /* protected fun createWebviewClient(): EditorWebViewClient? {
         return EditorWebViewClient()
     }*/

    fun setOnTextChangeListener(listener: OnTextChangeListener?) {
        mTextChangeListener = listener
    }

    fun setOnDecorationChangeListener(listener: OnDecorationStateListener?) {
        mDecorationStateListener = listener
    }

    fun setOnInitialLoadListener(listener: AfterInitialLoadListener?) {
        mLoadListener = listener
    }

    private fun callback(text: String) {
        mContents = CALLBACK_SCHEME?.toRegex()?.let { text.replaceFirst(it, "") }
        if (mTextChangeListener != null) {
            mTextChangeListener!!.onTextChange(mContents)
        }
    }

    private fun stateCheck(text: String) {
        val state = STATE_SCHEME?.toRegex()
            ?.let { text.replaceFirst(it, "").uppercase(Locale.ENGLISH) }
        val types: MutableList<Type> = ArrayList()
        for (type: Type in Type.values()) {
            if (TextUtils.indexOf(state, type.name) !== -1) {
                types.add(type)
            }
        }
        if (mDecorationStateListener != null) {
            mDecorationStateListener!!.onStateChangeListener(state, types)
        }
    }

    private fun applyAttributes(context: Context, attrs: AttributeSet) {
        val attrsArray = intArrayOf(
            R.attr.gravity
        )
        val ta: TypedArray = context.obtainStyledAttributes(attrs, attrsArray)
        val gravity: Int = ta.getInt(0, NO_ID)
        when (gravity) {
            Gravity.LEFT -> exec("javascript:RE.setTextAlign(\"left\")")
            Gravity.RIGHT -> exec("javascript:RE.setTextAlign(\"right\")")
            Gravity.TOP -> exec("javascript:RE.setVerticalAlign(\"top\")")
            Gravity.BOTTOM -> exec("javascript:RE.setVerticalAlign(\"bottom\")")
            Gravity.CENTER_VERTICAL -> exec("javascript:RE.setVerticalAlign(\"middle\")")
            Gravity.CENTER_HORIZONTAL -> exec("javascript:RE.setTextAlign(\"center\")")
            Gravity.CENTER -> {
                exec("javascript:RE.setVerticalAlign(\"middle\")")
                exec("javascript:RE.setTextAlign(\"center\")")
            }
        }
        ta.recycle()
    }

    fun setHtml(contents: String?) {
        var contents = contents
        if (contents == null) {
            contents = ""
        }
        try {
            exec(
                "javascript:RE.setHtml('" + URLEncoder.encode(contents, "UTF-8").toString() + "');"
            )
        } catch (e: UnsupportedEncodingException) {
            // No handling
        }
        mContents = contents
    }

    fun getHtml(): String? {
        return mContents
    }

    fun setEditorFontColor(color: Int) {
        val hex = convertHexColorString(color)
        exec("javascript:RE.setBaseTextColor('$hex');")
    }

    fun setEditorFontSize(px: Int) {
        exec("javascript:RE.setBaseFontSize('" + px + "px');")
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        exec(
            "javascript:RE.setPadding('" + left + "px', '" + top + "px', '" + right + "px', '" + bottom
                    + "px');"
        )
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        // still not support RTL.
        setPadding(start, top, end, bottom)
    }

    fun setEditorBackgroundColor(color: Int) {
        setBackgroundColor(color)
    }

    override fun setBackgroundResource(resid: Int) {
        val bitmap: Bitmap = Utils.decodeResource(context, resid)
        val base64: String = Utils.toBase64(bitmap)
        bitmap.recycle()
        exec("javascript:RE.setBackgroundImage('url(data:image/png;base64,$base64)');")
    }

    override fun setBackground(background: Drawable?) {
        val bitmap: Bitmap = Utils.toBitmap(background)
        val base64: String = Utils.toBase64(bitmap)
        bitmap.recycle()
        exec("javascript:RE.setBackgroundImage('url(data:image/png;base64,$base64)');")
    }

    fun setBackground(url: String) {
        exec("javascript:RE.setBackgroundImage('url($url)');")
    }

    fun setEditorWidth(px: Int) {
        exec("javascript:RE.setWidth('" + px + "px');")
    }

    fun setEditorHeight(px: Int) {
        exec("javascript:RE.setHeight('" + px + "px');")
    }

    fun setPlaceholder(placeholder: String) {
        exec("javascript:RE.setPlaceholder('$placeholder');")
    }

    fun setInputEnabled(inputEnabled: Boolean) {
        exec("javascript:RE.setInputEnabled($inputEnabled)")
    }

    fun loadCSS(cssFile: String) {
        val jsCSSImport = ("(function() {" +
                "    var head  = document.getElementsByTagName(\"head\")[0];" +
                "    var link  = document.createElement(\"link\");" +
                "    link.rel  = \"stylesheet\";" +
                "    link.type = \"text/css\";" +
                "    link.href = \"" + cssFile + "\";" +
                "    link.media = \"all\";" +
                "    head.appendChild(link);" +
                "}) ();")
        exec("javascript:$jsCSSImport")
    }

    fun undo() {
        exec("javascript:RE.undo();")
    }

    fun redo() {
        exec("javascript:RE.redo();")
    }

    fun setBold() {
        exec("javascript:RE.setBold();")
    }

    fun setItalic() {
        exec("javascript:RE.setItalic();")
    }

    fun setSubscript() {
        exec("javascript:RE.setSubscript();")
    }

    fun setSuperscript() {
        exec("javascript:RE.setSuperscript();")
    }

    fun setStrikeThrough() {
        exec("javascript:RE.setStrikeThrough();")
    }

    fun setUnderline() {
        exec("javascript:RE.setUnderline();")
    }

    fun setTextColor(color: Int) {
        exec("javascript:RE.prepareInsert();")
        val hex = convertHexColorString(color)
        exec("javascript:RE.setTextColor('$hex');")
    }

    fun setTextBackgroundColor(color: Int) {
        exec("javascript:RE.prepareInsert();")
        val hex = convertHexColorString(color)
        exec("javascript:RE.setTextBackgroundColor('$hex');")
    }

    fun setFontSize(fontSize: Int) {
        if (fontSize > 7 || fontSize < 1) {
            Log.e("RichEditor", "Font size should have a value between 1-7")
        }
        exec("javascript:RE.setFontSize('$fontSize');")
    }

    fun removeFormat() {
        exec("javascript:RE.removeFormat();")
    }

    fun setHeading(heading: Int) {
        exec("javascript:RE.setHeading('$heading');")
    }

    fun setIndent() {
        exec("javascript:RE.setIndent();")
    }

    fun setOutdent() {
        exec("javascript:RE.setOutdent();")
    }

    fun setAlignLeft() {
        exec("javascript:RE.setJustifyLeft();")
    }

    fun setAlignCenter() {
        exec("javascript:RE.setJustifyCenter();")
    }

    fun setAlignRight() {
        exec("javascript:RE.setJustifyRight();")
    }

    fun setBlockquote() {
        exec("javascript:RE.setBlockquote();")
    }

    fun setBullets() {
        exec("javascript:RE.setBullets();")
    }

    fun setNumbers() {
        exec("javascript:RE.setNumbers();")
    }

    fun insertImage(url: String, alt: String) {
        exec("javascript:RE.prepareInsert();")
        exec("javascript:RE.insertImage('$url', '$alt');")
    }

    /**
     * the image according to the specific width of the image automatically
     *
     * @param url
     * @param alt
     * @param width
     */
    fun insertImage(url: String, alt: String, width: Int) {
        exec("javascript:RE.prepareInsert();")
        exec("javascript:RE.insertImageW('$url', '$alt','$width');")
    }

    /**
     * [RichEditor.insertImage] will show the original size of the image.
     * So this method can manually process the image by adjusting specific width and height to fit into different mobile screens.
     *
     * @param url
     * @param alt
     * @param width
     * @param height
     */
    fun insertImage(url: String, alt: String, width: Int, height: Int) {
        exec("javascript:RE.prepareInsert();")
        exec("javascript:RE.insertImageWH('$url', '$alt','$width', '$height');")
    }

    fun insertVideo(url: String) {
        exec("javascript:RE.prepareInsert();")
        exec("javascript:RE.insertVideo('$url');")
    }

    fun insertVideo(url: String, width: Int) {
        exec("javascript:RE.prepareInsert();")
        exec("javascript:RE.insertVideoW('$url', '$width');")
    }

    fun insertVideo(url: String, width: Int, height: Int) {
        exec("javascript:RE.prepareInsert();")
        exec("javascript:RE.insertVideoWH('$url', '$width', '$height');")
    }

    fun insertAudio(url: String) {
        exec("javascript:RE.prepareInsert();")
        exec("javascript:RE.insertAudio('$url');")
    }

    fun insertYoutubeVideo(url: String) {
        exec("javascript:RE.prepareInsert();")
        exec("javascript:RE.insertYoutubeVideo('$url');")
    }

    fun insertYoutubeVideo(url: String, width: Int) {
        exec("javascript:RE.prepareInsert();")
        exec("javascript:RE.insertYoutubeVideoW('$url', '$width');")
    }

    fun insertYoutubeVideo(url: String, width: Int, height: Int) {
        exec("javascript:RE.prepareInsert();")
        exec("javascript:RE.insertYoutubeVideoWH('$url', '$width', '$height');")
    }

    fun insertLink(href: String, title: String) {
        exec("javascript:RE.prepareInsert();")
        exec("javascript:RE.insertLink('$href', '$title');")
    }

    fun insertTodo() {
        exec("javascript:RE.prepareInsert();")
        exec("javascript:RE.setTodo('" + Utils.getCurrentTime().toString() + "');")
    }

    fun focusEditor() {
        requestFocus()
        exec("javascript:RE.focus();")
    }

    fun clearFocusEditor() {
        exec("javascript:RE.blurFocus();")
    }

    private fun convertHexColorString(color: Int): String {
        return String.format("#%06X", (0xFFFFFF and color))
    }

    protected fun exec(trigger: String) {
        if (isReady) {
            load(trigger)
        } else {
            postDelayed(Runnable { exec(trigger) }, 100)
        }
    }

    private fun load(trigger: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, null)
        } else {
            loadUrl(trigger)
        }
    }

    /*   public class EditorWebViewClient() : WebViewClient() {
           override fun onPageFinished(view: WebView?, url: String) {
               isReady = url.equals(SETUP_HTML, ignoreCase = true)
               if (mLoadListener != null) {
                   mLoadListener.onAfterInitialLoad(isReady)
               }
           }

           override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
               val decode: String = Uri.decode(url)
               if (TextUtils.indexOf(url, CALLBACK_SCHEME) === 0) {
                   callback(decode)
                   return true
               } else if (TextUtils.indexOf(url, STATE_SCHEME) === 0) {
                   stateCheck(decode)
                   return true
               }
               return super.shouldOverrideUrlLoading(view, url)
           }

           @TargetApi(Build.VERSION_CODES.LOLLIPOP)
           override fun shouldOverrideUrlLoading(
               view: WebView?,
               request: WebResourceRequest
           ): Boolean {
               val url: String = request.getUrl().toString()
               val decode: String = Uri.decode(url)
               if (TextUtils.indexOf(url, CALLBACK_SCHEME) === 0) {
                   callback(decode)
                   return true
               } else if (TextUtils.indexOf(url, STATE_SCHEME) === 0) {
                   stateCheck(decode)
                   return true
               }
               return super.shouldOverrideUrlLoading(view, request)
           }
       }*/
}