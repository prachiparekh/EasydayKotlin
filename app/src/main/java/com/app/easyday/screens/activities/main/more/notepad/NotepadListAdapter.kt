package com.app.easyday.screens.activities.main.more.notepad

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.NoteInterface
import com.app.easyday.app.sources.remote.model.NoteResponse
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NotepadListAdapter (private val context: Context, private var notesList: ArrayList<NoteResponse>,
                          val anInterfaceClick: NoteInterface
) : RecyclerView.Adapter<NotepadListAdapter.ViewHolder>() {

    var pos: Int? = null
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = notesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotepadListAdapter.ViewHolder {
        val view = inflater.inflate(R.layout.note_pad_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotepadListAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val noteTitle = itemView.findViewById<TextView>(R.id.noteTitleTV)
        val noteDate = itemView.findViewById<TextView>(R.id.noteDateTV)
        val arrowIV = itemView.findViewById<ImageView>(R.id.arrowIV)

        @SuppressLint("NewApi")
        fun bind(position: Int) {
            val item = notesList[position]
            pos = position
            val odt = OffsetDateTime.parse(item.createdAt)
            val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
            val dtf1 = DateTimeFormatter.ofPattern("HH:MMa", Locale.ENGLISH)

            noteTitle.text = item.title
            noteDate.text = context.resources.getString(R.string.note_date, dtf.format(odt))

            arrowIV.setOnClickListener {
                anInterfaceClick.OnDeleteNoteClick(position)

            }
        }

    }

}