package ejektaflex.bountiful.data

import ejektaflex.bountiful.data.entry.BountyEntry
import ejektaflex.bountiful.generic.IIdentifiable


interface IEntryPool : IValueRegistry<BountyEntry>, IIdentifiable {
    val modsRequired: MutableList<String>?
}