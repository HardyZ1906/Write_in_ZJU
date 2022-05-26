package com.wzju.util;

import java.io.*;
import java.util.*;

// retain: Int(0), Int(num); insert: Int(1), UTF(str); delete: Int(2), Int(num)
public class OTUtils {

    private static class OPIterator {
        OTOperation[] op;
        int index = 0;
        
        OPIterator(OTOperation[] op) {
            this.op = op;
        }

        OTOperation nextOP() {
            if (index >= op.length) {
                return null;
            } else {
                return op[index++];
            }
        }
    }

    private static void append(ArrayList<OTOperation> op, OTOperation o) {
        if (op.isEmpty()) {
            op.add(o);
        } else {
            OTOperation tail = op.get(op.size() - 1);
            if (tail.type != o.type) {
                op.add(o);
            } else if (tail.type == OTOperation.RETAIN) {
                ((OTRetain)tail).length += ((OTRetain)o).length;
            } else if (tail.type == OTOperation.DELETE) {
                ((OTDelete)tail).length += ((OTDelete)o).length;
            } else {  // INSERT
                ((OTInsert)tail).content += ((OTInsert)o).content;
            }
        }
    }

    public static String apply(String before, OTOperation[] op) throws OTException {      
        StringBuilder after = new StringBuilder();
        
        int i = 0, baseLength = before.length();
        for (OTOperation o: op) {
            switch (o.type) {
                case OTOperation.RETAIN: {
                    int retainLength = ((OTRetain)o).length;
                    if (i + retainLength > baseLength) {
                        throw new OTException(OTException.INCORRECT_SIZE,
                            "Operand doesn't have enough characters to retain.");
                    }
                    after.append(before.substring(i, i += retainLength));
                    break;
                }
                case OTOperation.INSERT: {
                    after.append(((OTInsert)o).content);
                    break;
                }
                case OTOperation.DELETE: {
                    int deleteLength = ((OTDelete)o).length;
                    if (i + deleteLength > baseLength) {
                        throw new OTException(OTException.INCORRECT_SIZE,
                        "Operand doesn't have enough characters to delete.");
                    }
                    i += deleteLength;
                    break;
                }
            }
        }

        return after.toString();
    }

    public static void apply(OTOperation[] op, String fileName) throws OTException {      
        FileInputStream fis;
        FileOutputStream fos;
        String before, after;
        
        try {
            fis = new FileInputStream(fileName);
            before = new String(fis.readAllBytes());
            fis.close();
        } catch (FileNotFoundException e) {
            throw new OTException(OTException.FILE_NOT_FOUND, "File \"" + fileName + "\" does not exist.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new OTException(OTException.IO_FAILURE, "Encountered an IOException when reading from file \"" + fileName + "\".");
        }

        try {
            after = apply(before, op);
        } catch (OTException e) {
            throw new OTException(e.code, "Processing file \"" + fileName + "\": " + e.msg);
        }

        try {
            fos = new FileOutputStream(fileName);
            fos.write(after.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new OTException(OTException.IO_FAILURE, "Encountered an IOException when writing to file \"" + fileName + "\".");
        }
    }

    public static OTOperation[][] transform(OTOperation[] op1, OTOperation[] op2) throws OTException {
        ArrayList<OTOperation> op1t = new ArrayList<>();
        ArrayList<OTOperation> op2t = new ArrayList<>();

        OPIterator i1 = new OPIterator(op1), i2 = new OPIterator(op2);
        OTOperation o1 = i1.nextOP(), o2 = i2.nextOP();
        while (true) {
            if (o1 == null && o2 == null) {
                break;
            }

            if (o1 != null && o1.type == OTOperation.INSERT) {
                int insertLength = ((OTInsert)o1).content.length();
                op1t.add(o1);
                append(op2t, new OTRetain(insertLength));
                o1 = i1.nextOP();
                continue;
            } else if (o2 != null && o2.type == OTOperation.INSERT) {
                int insertLength = ((OTInsert)o2).content.length();
                op2t.add(o2);
                append(op1t, new OTRetain(insertLength));
                o2 = i2.nextOP();
                continue;
            }

            if (o1 == null) {
                throw new OTException(OTException.INCORRECT_SIZE, "The first operation is too short.");
            } else if (o2 == null) {
                throw new OTException(OTException.INCORRECT_OPERATION, "The second operation is too short.");
            }

            switch (o1.type * 3 + o2.type) {
                
                case 0: {  // RETAIN - RETAIN
                    OTRetain retain1 = (OTRetain)o1;
                    OTRetain retain2 = (OTRetain)o2;
                    int minLength;
                    
                    if (retain1.length == retain2.length) {
                        minLength = retain1.length;
                        o1 = i1.nextOP();
                        o2 = i2.nextOP();
                    } else if (retain1.length < retain2.length) {
                        minLength = retain1.length;
                        o1 = i1.nextOP();
                        o2 = new OTRetain(retain2.length - retain1.length);
                    } else {  // retain1.length > retqin2.length
                        minLength = retain2.length;
                        o2 = i2.nextOP();
                        o1 = new OTRetain(retain1.length - retain2.length);
                    }

                    append(op1t, new OTRetain(minLength));
                    append(op2t, new OTRetain(minLength));
                    
                    break;
                }
                
                case 2: {  // RETAIN - DELETE
                    OTRetain retain1 = (OTRetain)o1;
                    OTDelete delete2 = (OTDelete)o2;
                    int minLength;

                    if (retain1.length == delete2.length) {
                        minLength = retain1.length;
                        o1 = i1.nextOP();
                        o2 = i2.nextOP();
                    } else if (retain1.length < delete2.length) {
                        minLength = retain1.length;
                        o1 = i1.nextOP();
                        o2 = new OTDelete(delete2.length - retain1.length);
                    } else {  // retain1.length > delete2.length
                        minLength = delete2.length;
                        o2 = i2.nextOP();
                        o1 = new OTRetain(retain1.length - delete2.length);
                    }

                    append(op2t, new OTDelete(minLength));

                    break;
                }
                
                case 6: {  // DELETE - RETAIN
                    OTDelete delete1 = (OTDelete)o1;
                    OTRetain retain2 = (OTRetain)o2;
                    int minLength;

                    if (delete1.length == retain2.length) {
                        minLength = delete1.length;
                        o1 = i1.nextOP();
                        o2 = i2.nextOP();
                    } else if (delete1.length < retain2.length) {
                        minLength = delete1.length;
                        o1 = i1.nextOP();
                        o2 = new OTRetain(retain2.length - delete1.length);
                    } else {  // delete1.length > retain2.length
                        minLength = retain2.length;
                        o2 = i2.nextOP();
                        o1 = new OTDelete(delete1.length - retain2.length);
                    }

                    append(op1t, new OTDelete(minLength));

                    break;
                }
                
                case 8: {  // DELETE - DELETE
                    OTDelete delete1 = (OTDelete)o1;
                    OTDelete delete2 = (OTDelete)o2;

                    if (delete1.length == delete2.length) {
                        o1 = i1.nextOP();
                        o2 = i2.nextOP();
                    } else if (delete1.length < delete2.length) {
                        o1 = i1.nextOP();
                        o2 = new OTDelete(delete2.length - delete1.length);
                    } else {  // delete1.length > delete2.length
                        o2 = i2.nextOP();
                        o1 = new OTDelete(delete1.length - delete2.length);
                    }

                    break;
                }
            }
        }

        OTOperation[][] ot = new OTOperation[2][];
        
        ot[0] = op1t.toArray(new OTOperation[op1t.size()]);
        ot[1] = op2t.toArray(new OTOperation[op2t.size()]);
        
        return ot;
    }

    public static OTOperation[] compose(OTOperation[] op1, OTOperation[] op2) throws OTException {
        ArrayList<OTOperation> op = new ArrayList<>();

        OPIterator i1 = new OPIterator(op1), i2 = new OPIterator(op2);
        OTOperation o1 = i1.nextOP(), o2 = i2.nextOP();
        while (true) {
            if (o1 == null && o2 == null) {
                break;
            }

            if (o1 != null && o1.type == OTOperation.DELETE) {
                append(op, new OTDelete(((OTDelete)o1).length));
                o1 = i1.nextOP();
                continue;
            }

            if (o2 != null && o2.type == OTOperation.INSERT) {
                append(op, new OTInsert(((OTInsert)o2).content));
                o2 = i2.nextOP();
                continue;
            }

            if (o1 == null) {
                throw new OTException(OTException.INCORRECT_SIZE, "The first operation is too short.");
            } else if (o2 == null) {
                throw new OTException(OTException.INCORRECT_SIZE, "The second operation is too short.");
            }

            switch (o1.type * 3 + o2.type) {
                case 0: {  // RETAIN - RETAIN
                    OTRetain retain1 = (OTRetain)o1;
                    OTRetain retain2 = (OTRetain)o2;

                    if (retain1.length == retain2.length) {
                        append(op, new OTRetain(retain1.length));
                        o1 = i1.nextOP();
                        o2 = i2.nextOP();
                    } else if (retain1.length < retain2.length) {
                        append(op, new OTRetain(retain1.length));
                        o1 = i1.nextOP();
                        o2 = new OTRetain(retain2.length - retain1.length);
                    } else {  // retain1.length > retain2.length
                        append(op, new OTRetain(retain2.length));
                        o2 = i2.nextOP();
                        o1 = new OTRetain(retain1.length - retain2.length);
                    }

                    break;
                }

                case 2: {  // RETAIN - DELETE
                    OTRetain retain1 = (OTRetain)o1;
                    OTDelete delete2 = (OTDelete)o2;

                    if (retain1.length == delete2.length) {
                        append(op, new OTDelete(retain1.length));
                        o1 = i1.nextOP();
                        o2 = i2.nextOP();
                    } else if (retain1.length < delete2.length) {
                        append(op, new OTDelete(retain1.length));
                        o1 = i1.nextOP();
                        o2 = new OTDelete(delete2.length - retain1.length);
                    } else {  // retain1.length < delete2.length
                        append(op, new OTDelete(delete2.length));
                        o2 = i2.nextOP();
                        o1 = new OTRetain(retain1.length - delete2.length);
                    }

                    break;
                }

                case 3: {  // INSERT - RETAIN
                    OTInsert insert1 = (OTInsert)o1;
                    OTRetain retain2 = (OTRetain)o2;
                    int insertLength = insert1.content.length();

                    if (insertLength == retain2.length) {
                        append(op, new OTInsert(insert1.content));
                        o1 = i1.nextOP();
                        o2 = i2.nextOP();
                    } else if (insertLength < retain2.length) {
                        append(op, new OTInsert(insert1.content));
                        o1 = i1.nextOP();
                        o2 = new OTRetain(retain2.length - insertLength);
                    } else {  // insertLength > retain2.length
                        append(op, new OTInsert(insert1.content.substring(0, retain2.length)));
                        o2 = i2.nextOP();
                        o1 = new OTInsert(insert1.content.substring(retain2.length));
                    }

                    break;
                }

                case 5: {  // INSERT - DELETE
                    OTInsert insert1 = (OTInsert)o1;
                    OTDelete delete2 = (OTDelete)o2;
                    int insertLength = insert1.content.length();

                    if (insertLength == delete2.length) {
                        o1 = i1.nextOP();
                        o2 = i2.nextOP();
                    } else if (insertLength < delete2.length) {
                        o1 = i1.nextOP();
                        o2 = new OTDelete(delete2.length - insertLength);
                    } else {  // insertLength > delete2.length
                        o2 = i2.nextOP();
                        o1 = new OTInsert(insert1.content.substring(delete2.length));
                    }

                    break;
                }
            }
        }

        return op.toArray(new OTOperation[op.size()]);
    }

    private static OTOperation[] readOp(Scanner in) {             
        ArrayList<OTOperation> op = new ArrayList<>();

        boolean loop = true;
        while (loop) {
            System.out.println("Please enter the next operation: ");
            String o = in.next();
            switch (o.toLowerCase()) {
                case "retain": {
                    append(op, new OTRetain(in.nextInt()));
                    break;
                }                    
                case "insert": {
                    append(op, new OTInsert(in.next()));
                    break;
                }
                case "delete": {
                    append(op, new OTDelete(in.nextInt()));
                    break;
                } 
                default: {
                    loop = false;
                }
            }
        }

        return op.toArray(new OTOperation[op.size()]);
    }

    final static boolean __TEST_APPLY__ = false, __TEST_TRANSFORM__ = true, __TEST_COMPOSE__ = false;

    public static void main(String[] args) {
        
        Scanner in = new Scanner(System.in);
        
        if (__TEST_APPLY__) {
            String base;
            OTOperation[] op;

            System.out.println("========================================");
            System.out.println("Testing apply...");

            System.out.println("----------------------------------------");
            System.out.println("Please enter the base string:");
            base = in.nextLine();

            System.out.println("----------------------------------------");
            System.out.println("Reading a sequence of operations...");
            op = readOp(in);

            try {
                System.out.println("----------------------------------------");
                System.out.println("The result of applying op:");
                System.out.println(apply(base, op));
            } catch (OTException e) {
                System.out.println("In method apply: " + e.getMessage());
            }
        }

        if (__TEST_TRANSFORM__) {
            OTOperation[] op1, op2, op1t, op2t;
            String base;

            System.out.println("========================================");
            System.out.println("Testing transform...");

            System.out.println("----------------------------------------");
            System.out.println("Please enter the base string:");
            base = in.nextLine();

            System.out.println("----------------------------------------");
            System.out.println("Reading the first sequence of operations...");
            op1 = readOp(in);
            System.out.println("----------------------------------------");
            System.out.println("Reading the second sequence of operations...");
            op2 = readOp(in);

            try {
                OTOperation[][] tmp = transform(op1, op2);
                op1t = tmp[0];
                op2t = tmp[1];
            } catch (OTException e) {
                System.out.println("In method transform: " + e.getMessage());
                return;
            }
            System.out.println("----------------------------------------");
            System.out.println("Transform of the first sequence:");
            for (OTOperation o1: op1t) {
                System.out.println(o1.toString());
            }
            System.out.println("----------------------------------------");
            System.out.println("Transform of the second sequence:");
            for (OTOperation o2: op2t) {
                System.out.println(o2.toString());
            }

            try {
                System.out.println("----------------------------------------");
                System.out.println("The result of applying op1 and transform of op2:");
                System.out.println(apply(apply(base, op1), op2t));
            } catch (OTException e) {
                System.out.println("In method apply: " + e.getMessage());
            }
            try {
                System.out.println("----------------------------------------");
                System.out.println("The result of applying op2 and transform of op1:");
                System.out.println(apply(apply(base, op2), op1t));
            } catch (OTException e) {
                System.out.println("In method apply: " + e.getMessage());
            }
        }

        if (__TEST_COMPOSE__) {
            OTOperation[] op1, op2, op;
            String base;

            System.out.println("========================================");
            System.out.println("Testing compose...");

            System.out.println("----------------------------------------");
            System.out.println("Please enter the base string:");
            base = in.nextLine();

            System.out.println("----------------------------------------");
            System.out.println("Reading the first sequence of operations...");
            op1 = readOp(in);
            System.out.println("----------------------------------------");
            System.out.println("Reading the second sequence of operations...");
            op2 = readOp(in);

            try {
                op = compose(op1, op2);
            } catch (OTException e) {
                System.out.println("In method compose: " + e.getMessage());
                return;
            }
            System.out.println("----------------------------------------");
            System.out.println("Compose of the two sequences:");
            for (OTOperation o: op) {
                System.out.println(o.toString());
            }

            try {
                System.out.println("----------------------------------------");
                System.out.println("The result of applying op1 and op2:");
                System.out.println(apply(apply(base, op1), op2));
            } catch (OTException e) {
                System.out.println("In method apply: " + e.getMessage());
            }
            try {
                System.out.println("----------------------------------------");
                System.out.println("The result of applying the compose of op1 and op2:");
                System.out.println(apply(base, op));
            } catch (OTException e) {
                System.out.println("In method apply: " + e.getMessage());
            }
        }
    }
}
