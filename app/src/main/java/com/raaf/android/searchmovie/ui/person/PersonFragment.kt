package com.raaf.android.searchmovie.ui.person

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.raaf.android.searchmovie.R
import com.raaf.android.searchmovie.dataModel.rootJSON.PersonResponse
import com.raaf.android.searchmovie.backgroundJob.services.FirebaseStorageService
import com.raaf.android.searchmovie.ui.adapters.SpousesAdapter
import com.raaf.android.searchmovie.ui.home.swipeFragments.FilmSwipeItemAdapter
import com.raaf.android.searchmovie.ui.showToolbar
import com.squareup.picasso.Picasso
import java.util.*

private const val EXTRA_PERSON_ID = "personId"
private const val TAG = "PersonFragment"
private const val EXTRA_FACTS_TEXT = "facts"
private const val TYPE_COLLECTION = "type"
private const val SPOUSE_NAME = "name"
private const val SPOUSE_DETAIL = "detail"
private const val EXTRA_PERSON = "person"
private const val ADD_MY_PERSON_COLLECTION = "AMPC"
private const val DELETE_MY_PERSON_COLLECTION = "DMP"
private const val EXTRA_ITEM_ID = "itemId"

class PersonFragment : Fragment(), SpousesAdapter.OnSpouseClickListener {

    lateinit var personViewModel: PersonViewModel
    private lateinit var currentPerson: PersonResponse
    private var isOpen = false
    lateinit var translateProfessionKeys: Map<String, String>

    private lateinit var mainLayout: LinearLayout
    private lateinit var floatingButton: FloatingActionButton
    private lateinit var personImage: ImageView
    private lateinit var personNameRu: TextView
    private lateinit var personNameEn: TextView
    private lateinit var profession: TextView
    private lateinit var growth: TextView
    private lateinit var birth: TextView
    private lateinit var birth1: TextView
    private lateinit var birth2: TextView
    //private lateinit var cardPersonDivider:View

    //private lateinit var factsDivider: View
    private lateinit var factsLayout: LinearLayout
    private lateinit var factsText: TextView
    private lateinit var allFacts: TextView

    private lateinit var countOfChildren: Array<String>
    private lateinit var spousesLayout: LinearLayout
    private lateinit var spousesRV: RecyclerView
    private var spousesAdapter: SpousesAdapter? = null

    private lateinit var bestWorksDivider: View
    private lateinit var bestWorksTopDivider: View
    private lateinit var bestWorksIncludeLayout: ConstraintLayout
    private lateinit var categoryName: TextView
    private lateinit var bestWorksLayout: ConstraintLayout
    private lateinit var bestWorksRV: RecyclerView
    //private lateinit var clickableLayout: ConstraintLayout
    private lateinit var all: TextView

    private lateinit var open: TextView
    private lateinit var share: TextView

    private var personId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        personViewModel = ViewModelProvider(this).get(PersonViewModel::class.java)
        countOfChildren = resources.getStringArray(R.array.count_of_children)
        translateProfessionKeys = mapOf("WRITER" to getString(R.string.writer), "OPERATOR" to getString(R.string.operator), "EDITOR" to getString(R.string.editor), "COMPOSER" to getString(R.string.composer), "PRODUCER_USSR" to getString(R.string.producer_ussr), "HIMSELF" to getString(R.string.himself), "HERSELF" to getString(R.string.herself), "HRONO_TITR_MALE" to getString(R.string.hrono_titr_male), "HRONO_TITR_FEMALE" to getString(R.string.hrono_titr_female), "TRANSLATOR" to getString(R.string.translator), "DIRECTOR" to getString(R.string.director), "DESIGN" to getString(R.string.design), "PRODUCER" to getString(R.string.producer), "ACTOR" to getString(R.string.actor), "VOICE_DIRECTOR" to getString(R.string.voice_director), "UNKNOWN" to getString(R.string.unknown))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_person, container, false)
        mainLayout = view.findViewById(R.id.main_layout_person)
        floatingButton = view.findViewById(R.id.fab_person)
        personImage = view.findViewById(R.id.person_image)
        personImage.clipToOutline = true
        personNameRu = view.findViewById(R.id.person_name_ru)
        personNameEn = view.findViewById(R.id.person_name_en)
        profession = view.findViewById(R.id.person_profession)
        growth = view.findViewById(R.id.person_growth)
        birth = view.findViewById(R.id.person_birth)
        birth1 = view.findViewById(R.id.person_birth_1)
        birth2 = view.findViewById(R.id.person_birth_2)

        //cardPersonDivider = view.findViewById(R.id.card_person_bottom_divider)
        factsLayout = view.findViewById(R.id.facts_layout_person)
        factsText = view.findViewById(R.id.person_facts)
        allFacts = view.findViewById(R.id.all_facts_person)

        spousesLayout = view.findViewById(R.id.spouses_layout_person)
        spousesRV = view.findViewById(R.id.spouses_recycler_view)

        bestWorksTopDivider = view.findViewById(R.id.divider_facts_best_works)
        bestWorksDivider = view.findViewById(R.id.divider_best_works)
        bestWorksLayout = view.findViewById(R.id.best_works_layout)
        bestWorksIncludeLayout = view.findViewById(R.id.best_works_include_layout)
        categoryName = bestWorksIncludeLayout.findViewById(R.id.name_category)
        bestWorksRV = bestWorksIncludeLayout.findViewById(R.id.content_container)
        //clickableLayout = bestWorksIncludeLayout.findViewById(R.id.clickable_layout)
        all = bestWorksIncludeLayout.findViewById(R.id.all_text_view)

        open = view.findViewById(R.id.open_person)
        share = view.findViewById(R.id.share_person)

        bestWorksRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        spousesRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        all.visibility = GONE
        personViewModel.personLiveData.observe(
                viewLifecycleOwner,
                Observer { person ->
                    showToolbarTitle(requireActivity().findViewById(R.id.toolbar), person)
                    val filmList = mutableListOf<Int>()
//                    Log.e(TAG, person.toString())
                    if (person.films.count() > 30) person.films.subList(0, 29).forEach { filmList.add(it.filmId) }
                    else person.films?.forEach { filmList.add(it.filmId) }
                    personViewModel.fetchBestWorks(filmList)
                    fetchUI(person)
                }
        )
        personViewModel.bestWorksLiveData.observe(
                viewLifecycleOwner,
                Observer { list ->
                    //Log.e(TAG,"list best:" + list.size.toString())
                    if (list.isNotEmpty()) {
                        bestWorksDivider.visibility = VISIBLE
                        bestWorksTopDivider.visibility = VISIBLE
                        bestWorksLayout.visibility = VISIBLE
                        bestWorksRV.adapter = FilmSwipeItemAdapter(list, currentPerson.films, translateProfessionKeys)
                        if (list.count() >= 30) personViewModel.bestWorksLiveData.removeObservers(viewLifecycleOwner)
                    }
                }
        )
        personViewModel.statusFloatingButton.observe(
            viewLifecycleOwner,
            Observer { flag ->
                if (flag) {
                    isOpen = true
                    floatingButton.setImageDrawable(ContextCompat.getDrawable(this.requireContext(), R.drawable.outline_delete_24))
                }
            }
        )
        requireArguments().getInt(EXTRA_PERSON_ID).let { personId = it }
        personViewModel.fetchPersonById(personId)
        categoryName.text = getString(R.string.best_works)

        allFacts.setOnClickListener {
            if (currentPerson.facts.size >= 2) {
                var bundle = bundleOf(EXTRA_FACTS_TEXT to currentPerson.facts)
                view.findNavController().navigate(R.id.action_global_factsFragment, bundle)
            }
        }

        open.setOnClickListener { view ->
            val address = Uri.parse(currentPerson.webUrl)
            val openIntent = Intent(Intent.ACTION_VIEW, address)
            if (context?.let { openIntent.resolveActivity(it.packageManager) } != null) {
                startActivity(openIntent)
            }
        }

        share.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, makeShareText())
            if (context?.let { shareIntent.resolveActivity(it.packageManager) } != null) {
                startActivity(shareIntent)
            }
        }

        floatingButton.setOnClickListener {
            isClick()
        }

        //Log.e(TAG, "Current person is: $currentPerson")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        personViewModel.clearBestWorksCache()
    }

    override fun onSpouseClicked(personId: Int) : Boolean {
        var result = personViewModel.checkSpouse(personId)
        if (result) personViewModel.clearBestWorksCache()
        return result
    }

    private fun showToolbarTitle(toolbar: Toolbar, person: PersonResponse) {
        val language = Locale.getDefault().language
        when {
            language.contains("ru") -> showToolbar(toolbar, person.nameRu)
            language.contains("en") -> showToolbar(toolbar, person.nameEn)
        }
    }

    private fun deletePersonFromFirebase() {
        var intent = Intent(requireActivity(), FirebaseStorageService::class.java)
        intent.putExtra(TYPE_COLLECTION, DELETE_MY_PERSON_COLLECTION)
        intent.putExtra(EXTRA_ITEM_ID, personId.toString())
        requireActivity().startService(intent)
    }

    private fun sendDataToFirebase(type: String) {
        Log.e(TAG, currentPerson.toString())
        var intent = Intent(requireActivity(), FirebaseStorageService::class.java)
        intent.putExtra(TYPE_COLLECTION, type)
        intent.putExtra(EXTRA_PERSON, currentPerson)
        requireActivity().startService(intent)
    }

    private fun isClick() {
        if (!isOpen) {
            personViewModel.addToMyPerson(personId)
            sendDataToFirebase(ADD_MY_PERSON_COLLECTION)
            floatingButton.setImageDrawable(ContextCompat.getDrawable(this.requireContext(), R.drawable.outline_delete_24))
            makeToastFromAddPerson()
            isOpen = true
        }
        else {
            deletePersonFromFirebase()
            personViewModel.deleteToMyPerson(personId)
            floatingButton.setImageDrawable(ContextCompat.getDrawable(this.requireContext(), R.drawable.outline_add_24))
            makeToastFromDeletePerson()
            isOpen = false
        }
    }

    private fun fetchUI(person: PersonResponse) {
        currentPerson = person
        //Log.e(TAG, "fetchUI. CPN = ${currentPerson.nameRu}, CPID = ${currentPerson.personId}")
        var birthList = listOf<String>()
        //Log.e(TAG, person.toString())
        Picasso.get()
                .load(person.posterUrl)
                .fit()
                .into(personImage)
        visibilityText(personNameRu, person.nameRu, null)
        visibilityText(personNameEn, person.nameEn, null)
        visibilityText(profession, person.profession, null)
        visibilityText(growth, "${person.growth} см", null)
        if (person.birthday == null) person.birthday = ""
        if (person.birthplace == null) person.birthplace = ""
        if (person.birthplace!!.isNotEmpty()) {//Иногда проскакивало как null
            if (person.birthday!!.isNotEmpty() && person.age.toString().isNotEmpty() && person.age.toString() != "0") {
                birthList = person.birthplace!!.split(",")
                if (person.birthday!!.isNotEmpty() && person.birthday != null) birth1.text = "${person.birthday} ${birthList[0]},"
                else birth1.text = birthList[0]
                birthList = birthList.subList(1, birthList.lastIndex+1)
                birth2.text = "${birthList.toString().substring(1, birthList.toString().lastIndex)} (${person.age})"
            } else birthGone()
        } else birthGone()
        if (person.facts.isNotEmpty()) visibilityText(factsText, person.facts[0], factsLayout)
        else visibilityText(factsText, "", factsLayout)
        if (person.spouses.isNotEmpty()/* && person.spouses != null*/) {
            spousesAdapter = SpousesAdapter(person.spouses, countOfChildren.toList(), getString(R.string.divorce))
            spousesAdapter!!.setOnSpouseClickListener(this)
            spousesRV.adapter = spousesAdapter
            spousesLayout.visibility = VISIBLE
        }
        mainLayout.visibility = VISIBLE
        floatingButton.visibility = VISIBLE
    }

    private fun birthGone() {
        birth.visibility = GONE
        birth1.visibility = GONE
        birth2.visibility = GONE
    }

    private fun visibilityText(textView: TextView, text: String?, view: View?) {
        if (text != "" && text != " " && text != "null" && text != null && !text.contains("0 см")) textView.text = text
        else {
            textView.visibility = GONE
            if (view != null) view.visibility = GONE
        }
    }

    private fun makeShareText() : String {
        return if (currentPerson.nameEn.isNotBlank()) "\"${currentPerson.nameRu}\"(\"${currentPerson.nameEn}\") #kinopoisk\n${currentPerson.webUrl}"
        else "\"${currentPerson.nameRu}\" #kinpoisk\n${currentPerson.webUrl}"
    }


    private fun makeToastFromAddPerson() {
        Toast.makeText(this.context, R.string.add_person, Toast.LENGTH_SHORT).show()
    }

    private fun makeToastFromDeletePerson() {
        Toast.makeText(this.context, R.string.delete_person, Toast.LENGTH_SHORT).show()
    }
}