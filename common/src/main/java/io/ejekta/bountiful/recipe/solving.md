

Given an itemstack X, we know that there are multiple different Solutions for it.

Plan(stack x) = min(all Solution(x)) for solution of x

However, each Solution has different slots, and each slot could be multiple different kinds of stacks. So,

Solution(x) = sum(
    min(Plan(ingr_stack)) for ingr_stack in ingr
) of all ingrs



