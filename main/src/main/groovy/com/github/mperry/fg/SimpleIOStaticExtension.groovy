package com.github.mperry.fg

import fj.F
import fj.control.Trampoline
import fj.data.Stream
import groovy.transform.TypeChecked

/**
 * Created with IntelliJ IDEA.
 * User: MarkPerry
 * Date: 13/12/13
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
@TypeChecked
class SimpleIOStaticExtension {

    static <A> SimpleIO<Stream<A>> sequenceWhileR(SimpleIO clazz, Stream<SimpleIO<A>> stream, F<A, Boolean> f) {
        if (stream.empty) {
            SimpleIO.lift(Stream.nil())
        } else {
            stream.head().flatMap({ A a ->
                if (!f.f(a)) {
                    SimpleIO.lift(Stream.nil())
                } else {
                    def t = stream.tail()._1()
                    sequenceWhileR(clazz, t, f).map({ Stream<A> s -> s.cons(a)} as F<Stream<A>, Stream<A>>)
                }
            } as F)
        }
    }

    static <A> Trampoline<SimpleIO<Stream<A>>> empty() {
        Trampoline.pure(SimpleIO.lift(Stream.<A>nil()))
    }

    static <A> Trampoline<SimpleIO<Stream<A>>> sequenceWhileC(SimpleIO clazz, Stream<SimpleIO<A>> stream, F<A, Boolean> f) {
        if (stream.empty) {
            empty()
        } else {
            // add loop here
            def io = stream.head().map({ A a ->
                def b = f.f(a)
                if (!b) {
                    empty()
                } else {
                    def tail = stream.tail()._1()
                    sequenceWhileC(clazz, tail, f)
                }
            } as F<A, Trampoline<SimpleIO<Stream<A>>>>)
            SimpleIO.transform(io)
        }
    }

}
