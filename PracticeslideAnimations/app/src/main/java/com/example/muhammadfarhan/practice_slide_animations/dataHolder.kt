package com.example.muhammadfarhan.practice_slide_animations

import top.defaults.view.PickerView

/**
 * Created by MuhammadFarhan on 25/10/2018.
 */
class dataHolder : PickerView.PickerItem {

    var itemValue: String? = null


    constructor(s:String){
        this.itemValue = s
    }

    override fun getText(): String {
        return itemValue!!
    }

}