class ReferencedImage {
  PImage image;
  String reference;

  ReferencedImage(String p_path, String p_reference) {
    image = loadImage(p_path);
    reference = p_reference;
  }
}


PImage find_referencedImage(String reference) {
  for (ReferencedImage refim : images) {
    if (refim.reference.equals(reference)) return refim.image;
  }
  return null;
}


String figTypeToString(int figType) {
  switch (figType) {
    case PAWN: return "PAWN";
    case TOWER: return "TOWER";
    case HORSE: return "HORSE";
    case BISHOP: return "BISHOP";
    case QUEEN: return "QUEEN";
    case KING: return "KING";
    default: return null;
  }
}