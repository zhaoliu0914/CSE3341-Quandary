mutable Q main (mutable int arg) {
	mutable Ref list = randomList(arg + 42);
	while (arg > 1) {
        Q dummy = [ rmRandomEarlyElement(list) . rmRandomEarlyElement(list) ];
		arg = arg - 2;
	}
	return length(list);
}

mutable Q rmRandomEarlyElement(Ref list) {
	int index = randomInt(2);
	rmElement(list, index);
    return nil;
}

int length(Ref list) {
	if (isNil(list) != 0) return 0;
	return 1 + length((Ref)right(list));
}

Ref randomList(int length) {
	if (length == 0) {
        return nil;
    }
    return randomInt(100000) . randomList(length - 1);
}

mutable Q rmElement(Ref list, int index) {
    if (index == 0) {
        setLeft(list, left((Ref)right(list)));
        acq(list);
        setRight(list, right((Ref)right(list)));
        rel(list);
    } else if (index == 1) {
        acq(list);
        setRight(list, right((Ref)right(list)));
        rel(list);
    } else {
        acq(list);
        rmElement((Ref)right(list), index - 1);
        rel(list);
    }
    return nil;
}
