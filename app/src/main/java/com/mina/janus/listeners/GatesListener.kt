package com.mina.janus.listeners

import com.mina.janus.models.GatesModel

interface GatesListener {
    fun onGateClicked(checkedGates:GatesModel)
}