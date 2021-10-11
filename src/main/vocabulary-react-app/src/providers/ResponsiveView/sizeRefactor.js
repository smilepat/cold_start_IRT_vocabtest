/**
 * Scales view to scale.
 * @param {Object} view view나 view array로 스케일을 변경할 view
 * @param {Boolean} scaleToFit true면 크기에 맞춘다. false인 경우 desiredScale에 맞춘다.
 * @param {String} scaleToFitType default = 'fit' 다른옵션으로 ['width','height','default'] 중에 1개
 * @param {Number} desiredScale scale to define. not used if scale to fit is false 스케일을 설정하는것 같은데 뒷부분이 뭔가 이상
 * @param {Number} minimumScale 최소 scale 설정
 * @param {Number} maximumScale 최대 scale 설정
 * @param {Boolean} centerHorizontally true면 View가 수평(좌우)에서 중앙으로 간다.
 * @param {Boolean} centerVertically true면 View가 수직(상하)에서 중앙으로 간다.
 * @param {Number} horizontalPadding 수평(좌우)의 패딩을 설정
 * @param {Number} verticalPadding 수직(상하)의 패딩을 설정
 */
export const setViewScaleValue = ({
  view,
  scaleToFit = true,
  enableScaleUp = true,
  scaleToFitType = 'fit',
  desiredScale,
  minimumScale,
  maximumScale,
  centerHorizontally = true,
  centerVertically = true,
  horizontalPadding = 0,
  verticalPadding = 0,
}) => {
  if (!view) {
    // TODO:: throw error
    return new Error('ResponsiveView 에서 view 값은 필수입니다.')
  }

  let hasMinimumScale = !isNaN(minimumScale) && minimumScale != ''
  let hasMaximumScale = !isNaN(maximumScale) && maximumScale != ''
  let scaleNeededToFit = getViewFitToViewportScale(
    view,
    enableScaleUp,
    horizontalPadding,
    verticalPadding
  )
  let scaleNeededToFitWidth = getViewFitToViewportWidthScale(
    view,
    enableScaleUp,
    horizontalPadding
  )
  let scaleNeededToFitHeight = getViewFitToViewportHeightScale(
    view,
    enableScaleUp,
    verticalPadding
  )
  let scaleToFitFull = getViewFitToViewportScale(
    view,
    true,
    horizontalPadding,
    verticalPadding
  )
  let scaleToFitFullWidth = getViewFitToViewportWidthScale(
    view,
    true,
    horizontalPadding
  )
  let scaleToFitFullHeight = getViewFitToViewportHeightScale(
    view,
    true,
    verticalPadding
  )
  let scaleToWidth = scaleToFitType == 'width'
  let scaleToHeight = scaleToFitType == 'height'
  let topPosition = null
  let leftPosition = null
  let translateY = null
  let translateX = null
  let transformValue = ''
  let canCenterVertically = true
  let canCenterHorizontally = true
  let style = view.style

  if (scaleToFit) {
    if (scaleToFitType == 'fit' || scaleToFitType == '') {
      desiredScale = scaleNeededToFit
    } else if (scaleToFitType == 'width') {
      desiredScale = scaleNeededToFitWidth
    } else if (scaleToFitType == 'height') {
      desiredScale = scaleNeededToFitHeight
    }
  } else {
    if (isNaN(desiredScale)) {
      desiredScale = 1
    }
  }

  /**
   * scale to fit width
   */
  if (scaleToWidth && scaleToHeight == false) {
    canCenterVertically = scaleNeededToFitHeight >= scaleNeededToFitWidth
    canCenterHorizontally = scaleNeededToFitWidth >= 1 && enableScaleUp == false

    if (scaleToFit) {
      desiredScale = scaleNeededToFitWidth
    }

    if (hasMinimumScale) {
      desiredScale = Math.max(desiredScale, Number(minimumScale))
    }

    if (hasMaximumScale) {
      desiredScale = Math.min(desiredScale, Number(maximumScale))
    }

    desiredScale = getShortNumber(desiredScale)

    canCenterHorizontally = canCenterHorizontallyCheck(
      view,
      'width',
      enableScaleUp,
      desiredScale,
      minimumScale,
      maximumScale,
      horizontalPadding,
      verticalPadding
    )
    canCenterVertically = canCenterVerticallyCheck(
      view,
      'width',
      enableScaleUp,
      desiredScale,
      minimumScale,
      maximumScale,
      horizontalPadding,
      verticalPadding
    )

    if (desiredScale > 1 && enableScaleUp) {
      transformValue = 'scale(' + desiredScale + ')'
    } else if (desiredScale >= 1 && enableScaleUp == false) {
      transformValue = 'scale(' + 1 + ')'
    } else {
      transformValue = 'scale(' + desiredScale + ')'
    }

    if (centerVertically) {
      if (canCenterVertically) {
        translateY = '-50%'
        topPosition = '50%'
      } else {
        translateY = '0'
        topPosition = '0'
      }

      if (style.top != topPosition) {
        style.top = topPosition + ''
      }

      if (canCenterVertically) {
        transformValue += ' translateY(' + translateY + ')'
      }
    }

    if (centerHorizontally) {
      if (canCenterHorizontally) {
        translateX = '-50%'
        leftPosition = '50%'
      } else {
        translateX = '0'
        leftPosition = '0'
      }

      if (style.left != leftPosition) {
        style.left = leftPosition + ''
      }

      if (canCenterHorizontally) {
        transformValue += ' translateX(' + translateX + ')'
      }
    }
    style.transformOrigin = '0 0'
    style.transform = transformValue

    /*self.viewScale = desiredScale
    self.viewToFitWidthScale = scaleNeededToFitWidth
    self.viewToFitHeightScale = scaleNeededToFitHeight
    self.viewLeft = leftPosition
    self.viewTop = topPosition*/

    return desiredScale
  }

  /**
   *  scale to fit height
   **/
  if (scaleToHeight && scaleToWidth == false) {
    canCenterVertically = scaleNeededToFitHeight >= scaleNeededToFitWidth
    canCenterHorizontally = scaleNeededToFitWidth >= 1 && enableScaleUp == false

    if (scaleToFit) {
      desiredScale = scaleNeededToFitHeight
    }

    if (hasMinimumScale) {
      desiredScale = Math.max(desiredScale, Number(minimumScale))
    }

    if (hasMaximumScale) {
      desiredScale = Math.min(desiredScale, Number(maximumScale))
    }

    desiredScale = getShortNumber(desiredScale)

    canCenterHorizontally = canCenterHorizontallyCheck(
      view,
      'height',
      enableScaleUp,
      desiredScale,
      minimumScale,
      maximumScale,
      horizontalPadding,
      verticalPadding
    )
    canCenterVertically = canCenterVerticallyCheck(
      view,
      'height',
      enableScaleUp,
      desiredScale,
      minimumScale,
      maximumScale,
      horizontalPadding,
      verticalPadding
    )

    if (desiredScale > 1 && enableScaleUp) {
      transformValue = 'scale(' + desiredScale + ')'
    } else if (desiredScale >= 1 && enableScaleUp == false) {
      transformValue = 'scale(' + 1 + ')'
    } else {
      transformValue = 'scale(' + desiredScale + ')'
    }

    if (centerHorizontally) {
      if (canCenterHorizontally) {
        translateX = '-50%'
        leftPosition = '50%'
      } else {
        translateX = '0'
        leftPosition = '0'
      }

      if (style.left != leftPosition) {
        style.left = leftPosition + ''
      }

      if (canCenterHorizontally) {
        transformValue += ' translateX(' + translateX + ')'
      }
    }

    if (centerVertically) {
      if (canCenterVertically) {
        translateY = '-50%'
        topPosition = '50%'
      } else {
        translateY = '0'
        topPosition = '0'
      }

      if (style.top != topPosition) {
        style.top = topPosition + ''
      }

      if (canCenterVertically) {
        transformValue += ' translateY(' + translateY + ')'
      }
    }

    style.transformOrigin = '0 0'
    style.transform = transformValue

    /*self.viewScale = desiredScale
    self.viewToFitWidthScale = scaleNeededToFitWidth
    self.viewToFitHeightScale = scaleNeededToFitHeight
    self.viewLeft = leftPosition
    self.viewTop = topPosition*/

    return scaleNeededToFitHeight
  }

  /**
   * Scale to fit
   */
  if (scaleToFitType == 'fit') {
    canCenterVertically = scaleNeededToFitHeight >= scaleNeededToFit
    canCenterHorizontally = scaleNeededToFitWidth >= scaleNeededToFit

    if (hasMinimumScale) {
      desiredScale = Math.max(desiredScale, Number(minimumScale))
    }

    desiredScale = getShortNumber(desiredScale)

    if (scaleToFit == false) {
      canCenterVertically = scaleToFitFullHeight >= desiredScale
      canCenterHorizontally = desiredScale < scaleToFitFullWidth
    } else if (scaleToFit) {
      desiredScale = scaleNeededToFit
    }
    transformValue = 'scale(' + desiredScale + ')'

    if (centerVertically) {
      if (canCenterVertically) {
        translateY = '-50%'
        topPosition = '50%'
      } else {
        translateY = '0'
        topPosition = '0'
      }

      if (style.top != topPosition) {
        style.top = topPosition + ''
      }

      if (canCenterVertically) {
        transformValue += ' translateY(' + translateY + ')'
      }
    }

    if (centerHorizontally) {
      if (canCenterHorizontally) {
        translateX = '-50%'
        leftPosition = '50%'
      } else {
        translateX = '0'
        leftPosition = '0'
      }

      if (style.left != leftPosition) {
        style.left = leftPosition + ''
      }

      if (canCenterHorizontally) {
        transformValue += ' translateX(' + translateX + ')'
      }
    }

    style.transformOrigin = '0 0'
    style.transform = transformValue

    /*self.viewScale = desiredScale
    self.viewToFitWidthScale = scaleNeededToFitWidth
    self.viewToFitHeightScale = scaleNeededToFitHeight
    self.viewLeft = leftPosition
    self.viewTop = topPosition

    self.updateSliderValue(desiredScale)*/
    return desiredScale
  }

  /**
   * scaleToFitType 이 기본이거나 값이 없을때
   */
  if (scaleToFitType == 'default' || scaleToFitType == '') {
    desiredScale = 1

    if (hasMinimumScale) {
      desiredScale = Math.max(desiredScale, Number(minimumScale))
    }
    if (hasMaximumScale) {
      desiredScale = Math.min(desiredScale, Number(maximumScale))
    }

    canCenterHorizontally = canCenterHorizontallyCheck(
      view,
      'none',
      false,
      desiredScale,
      minimumScale,
      maximumScale,
      horizontalPadding,
      verticalPadding
    )
    canCenterVertically = canCenterVerticallyCheck(
      view,
      'none',
      false,
      desiredScale,
      minimumScale,
      maximumScale,
      horizontalPadding,
      verticalPadding
    )

    if (centerVertically) {
      if (canCenterVertically) {
        translateY = '-50%'
        topPosition = '50%'
      } else {
        translateY = '0'
        topPosition = '0'
      }

      if (style.top != topPosition) {
        style.top = topPosition + ''
      }

      if (canCenterVertically) {
        transformValue += ' translateY(' + translateY + ')'
      }
    }

    if (centerHorizontally) {
      if (canCenterHorizontally) {
        translateX = '-50%'
        leftPosition = '50%'
      } else {
        translateX = '0'
        leftPosition = '0'
      }

      if (style.left != leftPosition) {
        style.left = leftPosition + ''
      }

      if (canCenterHorizontally) {
        transformValue += ' translateX(' + translateX + ')'
      } else {
        transformValue += ' translateX(' + 0 + ')'
      }
    }

    style.transformOrigin = '0 0'
    style.transform = transformValue

    /*self.viewScale = desiredScale
    self.viewToFitWidthScale = scaleNeededToFitWidth
    self.viewToFitHeightScale = scaleNeededToFitHeight
    self.viewLeft = leftPosition
    self.viewTop = topPosition*/

    return desiredScale
  }
}

/**
 * Returns true if view can be centered horizontally
 * @param {HTMLElement} view view
 * @param {String} type type of scaling - width, height, all, none
 * @param {Boolean} scaleUp if scale up enabled
 * @param {Number} scale target scale value
 */
export const canCenterHorizontallyCheck = (
  view,
  type,
  scaleUp,
  scale,
  minimumScale,
  maximumScale,
  horizontalPadding,
  verticalPadding
) => {
  var scaleNeededToFit = getViewFitToViewportScale(
    view,
    scaleUp,
    horizontalPadding,
    verticalPadding
  )
  var scaleNeededToFitHeight = getViewFitToViewportHeightScale(
    view,
    scaleUp,
    horizontalPadding
  )
  var scaleNeededToFitWidth = getViewFitToViewportWidthScale(
    view,
    scaleUp,
    verticalPadding
  )
  var canCenter = false
  var minScale

  type = type == null ? 'none' : type
  scale = scale == null ? scale : scaleNeededToFitWidth
  scaleUp = scaleUp == null ? false : scaleUp

  if (type == 'width') {
    if (scaleUp && maximumScale == null) {
      canCenter = false
    } else if (scaleNeededToFitWidth >= 1) {
      canCenter = true
    }
  } else if (type == 'height') {
    minScale = Math.min(1, scaleNeededToFitHeight)
    if (minimumScale != '' && maximumScale != '') {
      minScale = Math.max(
        minimumScale,
        Math.min(maximumScale, scaleNeededToFitHeight)
      )
    } else {
      if (minimumScale != '') {
        minScale = Math.max(minimumScale, scaleNeededToFitHeight)
      }
      if (maximumScale != '') {
        minScale = Math.max(
          minimumScale,
          Math.min(maximumScale, scaleNeededToFitHeight)
        )
      }
    }

    if (scaleUp && maximumScale == '') {
      canCenter = false
    } else if (scaleNeededToFitWidth >= minScale) {
      canCenter = true
    }
  } else if (type == 'fit') {
    canCenter = scaleNeededToFitWidth >= scaleNeededToFit
  } else {
    if (scaleUp) {
      canCenter = false
    } else if (scaleNeededToFitWidth >= 1) {
      canCenter = true
    }
  }
  //self.horizontalScrollbarsNeeded = canCenter

  return canCenter
}

/**
 * Returns true if view can be centered horizontally
 * @param {HTMLElement} view view to scale
 * @param {String} type type of scaling
 * @param {Boolean} scaleUp if scale up enabled
 * @param {Number} scale target scale value
 */
export const canCenterVerticallyCheck = (
  view,
  type,
  scaleUp,
  scale,
  minimumScale,
  maximumScale,
  horizontalPadding,
  verticalPadding
) => {
  var scaleNeededToFit = getViewFitToViewportScale(
    view,
    scaleUp,
    horizontalPadding,
    verticalPadding
  )
  var scaleNeededToFitWidth = getViewFitToViewportWidthScale(
    view,
    scaleUp,
    horizontalPadding
  )
  var scaleNeededToFitHeight = getViewFitToViewportHeightScale(
    view,
    scaleUp,
    verticalPadding
  )
  var canCenter = false
  var minScale

  type = type == null ? 'none' : type
  scale = scale == null ? 1 : scale
  scaleUp = scaleUp == null ? false : scaleUp

  if (type == 'width') {
    canCenter = scaleNeededToFitHeight >= scaleNeededToFitWidth
  } else if (type == 'height') {
    minScale = Math.max(minimumScale, Math.min(maximumScale, scaleNeededToFit))
    canCenter = scaleNeededToFitHeight >= minScale
  } else if (type == 'fit') {
    canCenter = scaleNeededToFitHeight >= scaleNeededToFit
  } else {
    if (scaleUp) {
      canCenter = false
    } else if (scaleNeededToFitHeight >= 1) {
      canCenter = true
    }
  }
  //self.verticalScrollbarsNeeded = canCenter
  return canCenter
}

export const getViewFitToViewportScale = (
  view,
  scaleUp,
  horizontalPadding = 0,
  verticalPadding = 0
) => {
  let enableScaleUp = scaleUp
  let availableWidth =
    window.innerWidth ||
    document.documentElement.clientWidth ||
    document.body.clientWidth
  let availableHeight =
    window.innerHeight ||
    document.documentElement.clientHeight ||
    document.body.clientHeight
  let newScale = 1
  let elementWidth = parseFloat(getComputedStyle(view, 'style').width)
  let elementHeight = parseFloat(getComputedStyle(view, 'style').height)

  // if element is not added to the document computed values are NaN
  if (isNaN(elementWidth) || isNaN(elementHeight)) {
    return newScale
  }

  availableWidth -= horizontalPadding
  availableHeight -= verticalPadding

  if (enableScaleUp) {
    newScale = Math.min(
      availableHeight / elementHeight,
      availableWidth / elementWidth
    )
  } else if (elementWidth > availableWidth || elementHeight > availableHeight) {
    newScale = Math.min(
      availableHeight / elementHeight,
      availableWidth / elementWidth
    )
  }

  return newScale
}

export const getViewFitToViewportWidthScale = (
  view,
  scaleUp,
  horizontalPadding = 0
) => {
  let enableScaleUp = scaleUp
  let availableWidth =
    window.innerWidth ||
    document.documentElement.clientWidth ||
    document.body.clientWidth
  let newScale = 1
  let elementWidth = parseFloat(getComputedStyle(view, 'style').width)

  // if element is not added to the document computed values are NaN
  if (isNaN(elementWidth)) {
    return newScale
  }

  availableWidth -= horizontalPadding

  if (enableScaleUp) {
    newScale = availableWidth / elementWidth
  } else if (elementWidth > availableWidth) {
    newScale = availableWidth / elementWidth
  }
  return newScale
}

export const getViewFitToViewportHeightScale = (
  view,
  scaleUp,
  verticalPadding = 0
) => {
  let enableScaleUp = scaleUp
  let availableHeight =
    window.innerHeight ||
    document.documentElement.clientHeight ||
    document.body.clientHeight
  let newScale = 1
  let elementHeight = parseFloat(getComputedStyle(view, 'style').height)

  // if element is not added to the document computed values are NaN
  if (isNaN(elementHeight)) {
    return newScale
  }

  availableHeight -= verticalPadding

  if (enableScaleUp) {
    newScale = availableHeight / elementHeight
  } else if (elementHeight > availableHeight) {
    newScale = availableHeight / elementHeight
  }
  return newScale
}

export const getShortNumber = (value, places) => {
  if (places == null || places < 1) places = 4
  value = Math.round(value * Math.pow(10, places)) / Math.pow(10, places)
  return value
}

export default setViewScaleValue
