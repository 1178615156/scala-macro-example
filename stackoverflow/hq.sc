    trait Hello

    trait World

    type HelloWorld[X] = X with World

    trait Right extends (Hello) with World

    //Error:(9, 22) class type required but A$A65.this.Hello with A$A65.this.World found
    trait Error extends (HelloWorld[Hello])
