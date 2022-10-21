package com.app.easyday.app.sources.local.interfaces

import com.app.easyday.app.sources.remote.model.AttributeResponse

interface AttributeSelectionInterface {
    fun onClickAttribute(selectedAttrList: ArrayList<Int>, type:Int)
}