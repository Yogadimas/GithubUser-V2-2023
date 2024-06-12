package com.yogadimas.githubuser.ui.helper

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import com.yogadimas.githubuser.data.remote.model.ItemsItem
import com.yogadimas.githubuser.ui.view.activity.DetailActivity


fun showLoading(progressBar: ProgressBar, isLoading: Boolean) {
    progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
}


fun checkDataSize(
    it: ArrayList<ItemsItem?>?,
    textview: TextView,
    view: View,
    text: String,
    isFailed: Boolean,
) {

    if (!isFailed) {
        if (it?.size?.equals(0) == true) {
            textview.text = text
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    } else {
        view.visibility = View.GONE
    }

}


fun failedToConnectView(
    isFailed: Boolean,
    view: View,
) {
    if (isFailed) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }

}


fun goToDetail(packageContext: Context, login: String?): Intent {
    val goToDetail = Intent(packageContext, DetailActivity::class.java)
    goToDetail.putExtra(DetailActivity.EXTRA_KEY, login)
    return goToDetail
}

fun userInterfaceThemeState(isDarkModeActive: Boolean) {
    if (isDarkModeActive) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}

fun userInterfaceThemeState(isDarkModeActive: Boolean, switchMaterial: SwitchMaterial) {
    if (isDarkModeActive) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        switchMaterial.isChecked = true
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        switchMaterial.isChecked = false
    }
}

object SingleToast {
    private var mToast: Toast? = null
    fun show(context: Context, text: String?, duration: Int) {
        if (mToast != null) mToast?.cancel()
        mToast = Toast.makeText(context.applicationContext, text, duration)
        mToast?.show()
    }
}

