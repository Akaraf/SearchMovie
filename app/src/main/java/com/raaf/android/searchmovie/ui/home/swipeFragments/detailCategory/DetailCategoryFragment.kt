package com.raaf.android.searchmovie.ui.home.swipeFragments.detailCategory

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.homeItems.FilmSwipeItem
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse
import com.raaf.android.searchmovie.backgroundJob.services.FirebaseStorageService
import com.raaf.android.searchmovie.ui.adapters.MyPersonCategoryAdapter
import com.raaf.android.searchmovie.ui.showToolbar
import kotlin.math.roundToInt

//4 перехода: HomeFragment, ProfileFragment, SearchFragment buttons

private const val EXTRA_FIRST_TYPE = "firstType"
private const val EXTRA_SECOND_TYPE = "secondType"
private const val TAG = "DetailCategoryFragment"
private const val F_T_MY_FILMS_ALL = "my films"
private const val F_T_WATCHED = "Watched films"
private const val F_T_MY_STARS = "my stars"
private const val F_T_CATEGORIES = "Categories"
private const val TYPE_COLLECTION = "type"
private const val EXTRA_CATEGORY_ITEM_NAME = "categoryName"
private const val EXTRA_ITEM_ID = "itemId"
private const val DELETE_WATCH_LATER_COLLECTION = "DW"
private const val DELETE_FAVORITE_COLLECTION = "DF"
private const val F_T_COMPILATION = "Compilation"

class DetailCategoryFragment : Fragment(), DetailCategoryAdapter.OnDeleteClickListener, MyPersonCategoryAdapter.OnDeleteClickListener {

    private lateinit var detailCategoryViewModel: DetailCategoryViewModel

    private lateinit var noContentTextView: TextView

    private lateinit var firstCategoryLayout: LinearLayout
    private lateinit var detailCategoryRecyclerView1: RecyclerView
    private lateinit var nameCategoryTV1: TextView
    private lateinit var showImage1: ImageView
    private lateinit var categoryLayout1: LinearLayout
    private var statusShowImage1 = false

    private lateinit var secondCategoryLayout: LinearLayout
    private lateinit var detailCategoryRecyclerView2: RecyclerView
    private lateinit var nameCategoryTV2: TextView
    private lateinit var showImage2: ImageView
    private lateinit var categoryLayout2: LinearLayout
    private var statusShowImage2 = false

    private lateinit var firstType: String
    private lateinit var secondType: String
    private var categoryItemName = ""

    private lateinit var nameCategoryOfMyFilmsList: List<String>
    private val nameCategoriesMF = listOf("Watch later", "Favorite movies")
    private lateinit var watchedNameCategoryForUI: String
    private lateinit var myPersonNameCategory: String
    private var deleteIcon: Drawable? = null
    var margin = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailCategoryViewModel = ViewModelProvider(this).get(DetailCategoryViewModel::class.java)
        nameCategoryOfMyFilmsList = listOf(getString(R.string.favorite_movies), getString(R.string.watch_later))
        watchedNameCategoryForUI = getString(R.string.history)
        myPersonNameCategory = getString(R.string.my_stars)
        detailCategoryViewModel.provideCategoryNames(listOf(nameCategoriesMF[1], nameCategoriesMF[0], F_T_WATCHED, myPersonNameCategory))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_detail_category, container, false)
        noContentTextView = root.findViewById(R.id.f_d_c_no_content_text_view)
        firstCategoryLayout = root.findViewById(R.id.layout_for_first_categories)
        detailCategoryRecyclerView1 = root.findViewById(R.id.detail_category_recycler)
        detailCategoryRecyclerView1.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        nameCategoryTV1 = root.findViewById(R.id.detail_category_name)
        showImage1 = root.findViewById(R.id.show_image)
        secondCategoryLayout = root.findViewById(R.id.layout_for_two_categories)
        detailCategoryRecyclerView2 = root.findViewById(R.id.detail_category_recycler_2)
        detailCategoryRecyclerView2.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        nameCategoryTV2 = root.findViewById(R.id.detail_category_name_2)
        showImage2 = root.findViewById(R.id.show_image_2)
        categoryLayout1 = root.findViewById(R.id.detail_category_name_layout_1)
        categoryLayout2 = root.findViewById(R.id.detail_category_name_layout_2)
        deleteIcon = ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_baseline_delete_24)
        margin = resources.getDimension(R.dimen.margin_16).roundToInt()
        firstType = requireArguments().getString(EXTRA_FIRST_TYPE) ?: ""
        secondType = requireArguments().getString(EXTRA_SECOND_TYPE) ?: ""
        detailCategoryViewModel.typeCategory(firstType, secondType)
        if (firstType == F_T_CATEGORIES) {
            categoryItemName = requireArguments().getString(EXTRA_CATEGORY_ITEM_NAME) ?: ""
            detailCategoryViewModel.fetchCategoryFilms(secondType, categoryItemName)
        }
        makeToolbarTitle(firstType)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (detailCategoryViewModel.resultLiveData != null) detailCategoryViewModel.resultLiveData!!.observe(
                viewLifecycleOwner,
                Observer { items ->
                    if (firstType != F_T_CATEGORIES) detailCategoryViewModel.resultLiveData!!.removeObservers(viewLifecycleOwner)//Отписываемся, если myfilms
                    if (firstType == F_T_CATEGORIES) {
                        nameCategoryTV1.text = categoryItemName
                        detailCategoryRecyclerView1.adapter = DetailCategoryAdapter(items as MutableList<FilmSwipeItem>, categoryItemName, "")
                    } else {
                        if (items.isNotEmpty()) setDataForRecycler(items, null)
                        else {
                            hideFirstCategory()
                            if (firstType == F_T_MY_FILMS_ALL) noContentTextView.text = getString(R.string.no_content)
                            noContentTextView.visibility = VISIBLE
                        }
                    }
                }
        )
        if (detailCategoryViewModel.resultLiveDataForPersonResponse != null) detailCategoryViewModel.resultLiveDataForPersonResponse!!.observe(
            viewLifecycleOwner,
            Observer { items ->
                if (items.isNotEmpty()) setDataForRecycler(null, items)
                else {
                    hideFirstCategory()
                    if (firstType == F_T_MY_STARS) noContentTextView.text = getString(R.string.no_content_in_my_persons)
                    noContentTextView.visibility = VISIBLE
                }
                detailCategoryViewModel.resultLiveDataForPersonResponse!!.removeObservers(viewLifecycleOwner)
            }
        )

        categoryLayout1.setOnClickListener {
            if (statusShowImage1) {
                detailCategoryRecyclerView1.visibility = VISIBLE
                showImage1.setImageResource(R.drawable.outline_expand_less_24)
                statusShowImage1 = false
            } else {
                detailCategoryRecyclerView1.visibility = GONE
                showImage1.setImageResource(R.drawable.outline_expand_more_24)
                statusShowImage1 = true
            }
        }

        categoryLayout2.setOnClickListener {
            if (statusShowImage2) {
                detailCategoryRecyclerView2.visibility = VISIBLE
                showImage2.setImageResource(R.drawable.outline_expand_less_24)
                statusShowImage2 = false
            } else {
                detailCategoryRecyclerView2.visibility = GONE
                showImage2.setImageResource(R.drawable.outline_expand_more_24)
                statusShowImage2 = true
            }
        }
    }

    override fun onDestroyView() {
        if (firstType == F_T_CATEGORIES) detailCategoryViewModel.clearCategoryCacheDB()
        super.onDestroyView()
    }

    private fun receiveConsistentItems(list: List<FilmSwipeItem>, sT: String) : MutableList<FilmSwipeItem>{
        val listResult = mutableListOf<FilmSwipeItem>()
        list.forEach {
            if (it.parentId.contains(sT)) listResult.add(it)
        }
        if (listResult.isEmpty() && sT == nameCategoriesMF[1]) hideFirstCategory()
        if (listResult.isEmpty() && sT == nameCategoriesMF[0]) hideSecondCategory()
        return listResult
    }

    private fun setDataForRecycler(filmItems: List<FilmSwipeItem>?, personItems: MutableList<PersonResponse>?) {
        if (firstType == F_T_MY_FILMS_ALL) {
            showImage1.visibility = VISIBLE
            Toast.makeText(context, R.string.swipe_toast, Toast.LENGTH_SHORT).show()
            secondCategoryLayout.visibility = VISIBLE
            nameCategoryTV1.text = nameCategoryOfMyFilmsList[0]
            nameCategoryTV2.text = nameCategoryOfMyFilmsList[1]
            var adapter1 = DetailCategoryAdapter(receiveConsistentItems(filmItems!!, nameCategoriesMF[1]), nameCategoriesMF[1], DELETE_FAVORITE_COLLECTION)
            adapter1.setOnDeleteClickListener(this)
            var adapter2 = DetailCategoryAdapter(receiveConsistentItems(filmItems, nameCategoriesMF[0]), nameCategoriesMF[0], DELETE_WATCH_LATER_COLLECTION)
            adapter2.setOnDeleteClickListener(this)
            detailCategoryRecyclerView1.adapter = adapter1
            detailCategoryRecyclerView2.adapter = adapter2
            var callback1 = makeCallback(adapter1, null)
            var callback2 = makeCallback(adapter2, null)
            var touchHelper1 = ItemTouchHelper(callback1)
            var touchHelper2 = ItemTouchHelper(callback2)
            touchHelper1.attachToRecyclerView(detailCategoryRecyclerView1)
            touchHelper2.attachToRecyclerView(detailCategoryRecyclerView2)
        } else {
            categoryLayout1.setOnClickListener {

            }
            var adapter: DetailCategoryAdapter? = null
            var adapterForPerson: MyPersonCategoryAdapter? = null
            if (filmItems != null) adapter = DetailCategoryAdapter(receiveConsistentItems(filmItems!!, secondType), secondType, "")
            else {
                var collection: MutableSet<PersonResponse> = mutableSetOf<PersonResponse>()
                collection.addAll(personItems!!)
                adapterForPerson = MyPersonCategoryAdapter(collection, myPersonNameCategory)
                adapterForPerson.setOnDeleteClickListener(this)
            }
            //adapter.setOnDeleteClickListener(this)
            if (firstType == F_T_WATCHED) {
                adapter = DetailCategoryAdapter(receiveConsistentItems(filmItems!!, F_T_WATCHED), F_T_WATCHED, "")
                //adapter.setOnDeleteClickListener(this)
            }
            if (firstType == F_T_MY_STARS) {
                adapterForPerson!!.setOnDeleteClickListener(this)
            }
            nameCategoryTV1.text = secondType
            if (firstType == F_T_MY_STARS) detailCategoryRecyclerView1.adapter = adapterForPerson
            else detailCategoryRecyclerView1.adapter = adapter
            if (firstType == F_T_WATCHED) {
                //Toast.makeText(context, R.string.swipe_toast, Toast.LENGTH_LONG).show()
                nameCategoryTV1.text = watchedNameCategoryForUI
                //var callback = makeCallback(adapter, null)
                //var touchHelper = ItemTouchHelper(callback)
                //touchHelper.attachToRecyclerView(detailCategoryRecyclerView1)
            }
            if (firstType == F_T_MY_STARS) {
                Toast.makeText(context, R.string.swipe_toast, Toast.LENGTH_SHORT).show()
                nameCategoryTV1.text = myPersonNameCategory
                var callback = makeCallback(null, adapterForPerson)
                var touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(detailCategoryRecyclerView1)
            }
        }
    }

    private fun makeToolbarTitle(type: String) {
        var toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        when {
            type.contains(F_T_MY_FILMS_ALL) -> {
                showToolbar(toolbar, getString(R.string.my_films))
            }
            type.contains(F_T_MY_STARS) -> showToolbar(toolbar, getString(R.string.my_stars))
            type.contains(F_T_CATEGORIES) -> showToolbar(toolbar, categoryItemName)
            type.contains(F_T_WATCHED) -> showToolbar(toolbar, watchedNameCategoryForUI)
            type.contains(F_T_COMPILATION) -> {
                val title = when  {
                    secondType.contains("comedy") -> getString(R.string.comedy)
                    secondType.contains("fantasy") -> getString(R.string.fantasy)
                    secondType.contains("cartoon") -> getString(R.string.cartoon)
                    secondType.contains("drama") -> getString(R.string.drama)
                    secondType.contains("action") -> getString(R.string.action)
                    else -> ""
                }
                showToolbar(toolbar, title)
            }
            else -> showToolbar(toolbar, secondType)
        }
    }

    private fun hideFirstCategory() {
        firstCategoryLayout.visibility = GONE
    }

    private fun hideSecondCategory() {
        secondCategoryLayout.visibility = GONE
    }

    private fun makeCallback(adapter: DetailCategoryAdapter?, adapterForPerson: MyPersonCategoryAdapter?): ItemTouchHelper.SimpleCallback {
        var callback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (adapter != null) adapter.onItemDismiss(viewHolder.adapterPosition)
                else adapterForPerson!!.onItemDismiss(viewHolder.adapterPosition)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - deleteIcon!!.intrinsicHeight) / 2
                val iconBottom = iconTop + deleteIcon!!.intrinsicHeight
                val iconLeft = itemView.right - iconMargin - deleteIcon!!.intrinsicWidth
                val iconRight = itemView.right - iconMargin

                // swiping from right to left
                if (dX < 0) {
                    val background = ColorDrawable(Color.LTGRAY)
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    background.draw(c)
                    // Draw the delete icon
                    if (dX < (-itemView.width/5).toFloat()) {
                        deleteIcon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        deleteIcon!!.draw(c)
                    }
                }
            }
        }
        return callback
    }

    override fun onDeleteClick(endId: String, categoryName: String, type: String, itemId: String) {
        detailCategoryViewModel.deleteFromDB(endId,categoryName)
        sendDataForFirebaseService(type, itemId)
    }

    private fun sendDataForFirebaseService(type: String, itemId: String) {
        var intent = Intent(requireActivity(), FirebaseStorageService::class.java)
        intent.putExtra(TYPE_COLLECTION, type)
        intent.putExtra(EXTRA_ITEM_ID, itemId)
        requireActivity().startService(intent)
    }
}