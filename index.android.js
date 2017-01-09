'use strict'

import { NativeModules } from 'react-native'
// name as defined via ReactContextBaseJavaModule's getName
const NativeNotification = NativeModules.SNotification

function parseOptions(opt) {
    const attributes = { ...opt }
    if(!opt.smallIcon) attributes.smallIcon = 'ic_launcher'
    return attributes
}

const Notification = {
    showNotification(opt) {
        NativeNotification.show(parseOptions(opt))
    },
    updateNotificationScheduled(opt, delay) {
        NativeNotification.scheduleUpdate(parseOptions(opt), delay)
    },
    clearNotification() {
        NativeNotification.clear()
    },
}

export default Notification
