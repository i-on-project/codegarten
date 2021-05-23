type Delivery = {
    id: number,
    number: number,
    tag: string,
    dueDate?: string,
    isDue: boolean,
    isDelivered?: boolean,

    canManage: boolean,
}

type Deliveries = {
    deliveries: Delivery[],
    page: number,
    isLastPage: boolean,

    canManage: boolean,    
}