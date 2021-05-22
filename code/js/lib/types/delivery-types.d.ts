type Delivery = {
    id: number,
    number: number,
    tag: string,
    dueDate?: Date,

    canManage: boolean,
}

type Deliveries = {
    deliveries: Delivery[],
    page: number,
    isLastPage: boolean,

    canCreate: boolean,    
}