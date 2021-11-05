package com.raaf.android.searchmovie.ui.film
//This interface was copied from https://rmcreative.ru/blog/post/android-obrabatyvaem-nazhatie-back-v-fragmentakh
interface OnBackPressedListener {
    abstract fun onBackPressedL()

    abstract fun getFlag(): Boolean
}