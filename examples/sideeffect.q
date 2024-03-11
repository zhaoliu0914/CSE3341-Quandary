mutable Q main(int arg) {
  Ref c = 2 . 3;
  int result = (int)swapAndGetLeft(c) + (int)left(c);
  return result;
}

mutable Ref swap(Ref c) {
  Q tmp = left(c);
  setLeft(c, right(c));
  setRight(c, tmp);
  return c;
}

mutable Q swapAndGetLeft(Ref c) {
  Q tmp = left(c);
  setLeft(c, right(c));
  setRight(c, tmp);
  Q result = left(c);
  return result;
}  
