package com.github.mperry.fg

import fj.F

/**
 * Created with IntelliJ IDEA.
 * User: MarkPerry
 * Date: 20/12/13
 * Time: 8:45 AM
 * To change this template use File | Settings | File Templates.
 */
class FStaticExtension {

    public static <A, B> F<A, B> unit(F f, Closure<B> closure) {
        closure as F<A, B>
    }

}