package com.github.mperry.fg

import fj.F
import fj.F2
import fj.Unit
import groovy.transform.TypeChecked

//import groovy.transform.TypeChecked
//import groovy.transform.TypeCheckingMode

/**
 * Created by MarkPerry on 16/01/14.
 */
@TypeChecked
class ListJavaExtension2 {

//    static ListMonad monad() {
//        new ListMonad()
//    }

    static <A, B, C> List<A> map2(List<A> listA, List<B> listB, F2<A, B, C> f) {
        new ListMonad().map2(listA, listB, f)
    }

    static <A, B> List<B> as_(List<A> list, B b) {
        new ListMonad().as_(list, b)
    }

    static <A> List<Unit> skip(List<A> list) {
        new ListMonad().skip(list)
    }

    static <A> List<List<A>> sequence(List<List<A>> list) {
        new ListMonad().sequence(list)
    }

    static <A, B> List<List<B>> traverse(List<A> list, F<A, List<B>> f) {
        new ListMonad().traverse(list, f)
    }

    static <A, B> List<List<B>> replicateM(List<A> list, Integer n) {
        new ListMonad().replicateM(n, list)
    }



}
