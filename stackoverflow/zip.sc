import slick.collection.heterogeneous.HList

trait NodeTypes {

      trait Node {
        def allNodesHaveThis: Int
      }

    }

    object NodeTypes extends NodeTypes

    trait ScrumptiousTypes extends NodeTypes {

      trait Node {
        def scrumptiousness: Int
      }

      type ScrumptiousTypesNode = this.Node
    }

    object ScrumptiousTypes extends ScrumptiousTypes

    trait YummyTypes extends NodeTypes {

      trait Node {
        def yumminess: Int
      }

      type YummyTypesNode = this.Node
    }

    object YummyTypes extends YummyTypes

    trait Nodes {

      trait Nodes extends NodeTypes.Node with  YummyTypes.Node with ScrumptiousTypes.Node

    }


    object Graph extends  Nodes {

      case class Nodes() extends super.Nodes {
        override def yumminess: Int = 1
    //
        override def scrumptiousness: Int = 2

        override def allNodesHaveThis: Int = 3
      }

    }

HList