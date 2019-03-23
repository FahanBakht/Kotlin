package com.farhan.moviepocket.adapter

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.farhan.moviepocket.R
import com.farhan.moviepocket.model.Data
import com.farhan.moviepocket.utils.DynamicHeightImageView
import timber.log.Timber
import java.io.FileNotFoundException
import java.util.ArrayList

class MoviesAdapter : RecyclerView.Adapter<MoviesAdapter.ViewHolder>(), Filterable {

    private var context: Context? = null
    private var moviesArrayList: ArrayList<Data> = arrayListOf()
    private var backUpMoviesArrayList: ArrayList<Data> = arrayListOf()
    private var lastPosition = -1

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        context = p0.context
        val layout = R.layout.item_view_main_movie_list
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layout, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return moviesArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        holder.bindView(moviesArrayList[p1])

        if (holder.adapterPosition > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom)
            holder.itemView.startAnimation(animation)
            lastPosition = holder.adapterPosition
        }else{
            val animation = AnimationUtils.loadAnimation(context, R.anim.bottom_from_up)
            holder.itemView.startAnimation(animation)
            lastPosition = holder.adapterPosition
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                moviesArrayList = if (charString.isEmpty()) {
                    backUpMoviesArrayList
                } else {
                    val filteredList = arrayListOf<Data>()
                    for (row in backUpMoviesArrayList) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.title.toLowerCase().contains(charString.toLowerCase()) || row.genre.toLowerCase().contains(
                                charString.toLowerCase()
                            ) || row.year.toLowerCase().contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = moviesArrayList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var movieTittle: TextView = itemView.findViewById(R.id.movie_tittle)
        private var releaseDate: TextView = itemView.findViewById(R.id.movie_release_date)
        private var moviePoster: DynamicHeightImageView = itemView.findViewById(R.id.thumbnail)
        private var movieGenre: TextView = itemView.findViewById(R.id.movie_genre)
        private var loadingIndicatorView: com.wang.avi.AVLoadingIndicatorView =
            itemView.findViewById(R.id.item_view_image_loading_indicator)

        fun bindView(objMovie: Data) {
            releaseDate.text = objMovie.year
            movieGenre.text = objMovie.genre

            try {
                Glide.with(itemView.context)
                    .asBitmap()
                    .load(objMovie.poster)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .apply(RequestOptions().error(R.drawable.error_blank_image))
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            loadingIndicatorView.visibility = View.GONE
                            moviePoster.visibility = View.GONE
                            movieTittle.visibility = View.VISIBLE
                            Timber.e("${e?.stackTrace}")
                            movieTittle.text = objMovie.title
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            model: Any?,
                            target: Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            loadingIndicatorView.visibility = View.GONE
                            moviePoster.visibility = View.VISIBLE
                            movieTittle.visibility = View.GONE
                            val ratio = resource.width.toFloat() / resource.height
                            moviePoster.setAspectRatio(ratio)
                            //moviePoster.setImageBitmap(resource)
                            return false
                        }

                    }).into(moviePoster)
            } catch (e: FileNotFoundException) {
                Timber.e("catch")
                Glide.with(itemView.context)
                    .asBitmap()
                    .load(R.drawable.error_blank_image)
                    .into(moviePoster)
            }

        }
    }

    fun setMovieData(moviesArrayList: ArrayList<Data>) {
        if (this.moviesArrayList.size != 0 && this.moviesArrayList.isNotEmpty()) {
            this.moviesArrayList.clear()
        }
        backUpMoviesArrayList = moviesArrayList
        this.moviesArrayList = moviesArrayList
        notifyDataSetChanged()
    }

    fun clearMoviesData() {
        this.moviesArrayList.clear()
        notifyDataSetChanged()
    }

}