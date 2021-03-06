package com.github.mperry.fg

import fj.F
import fj.P2
import groovy.transform.Canonical
import groovy.transform.TypeChecked

/**
 * Created by MarkPerry on 9/01/14.
 */
@TypeChecked
@Canonical
class StateInt<A> extends State<Integer, A> {

    StateInt(F<Integer, P2<Integer, A>> f) {
        run = f
    }

    def <B> StateInt<B> flatMap(F<A, StateInt<B>> f) {
        new StateInt<B>({ Integer s ->
            def p = run.f(s)
            def a = p._2()
            def s2 = p._1()

            def sib = f.f(a)
            sib.run.f(s2)
        } as F)
    }

}
