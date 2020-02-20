package ejektaflex.bountiful.api.data

import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.generic.IIdentifiable


interface IEntryPool : IValueRegistry<BountyEntry<*>>, IIdentifiable